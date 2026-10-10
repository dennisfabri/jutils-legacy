# Windows can be larger than the screen at high display scaling

## Problem

JAuswertung runs on PCs with Windows display scaling of 166 % or 200 %. Window sizes in Swing are given in
*logical* pixels, and the logical screen shrinks with the scaling factor:

| Screen | 100 % | 166 % | 200 % |
|---|---|---|---|
| 1920×1080 | 1920×1080 | ≈1157×650 | 960×540 |
| 2560×1440 | 2560×1440 | ≈1542×867 | 1280×720 |

Minus the taskbar, a Full-HD screen at 200 % leaves about **960×515** usable logical pixels.

Many windows were sized in the 2000s for 1024/1280-pixel screens (800×600, minimum heights of 600–800). At high
scaling they no longer fit on the screen: the bottom part, often with the OK/Close buttons, ends up below the screen
edge. Nothing in jutils prevents this:

- `WindowUtils.setSize(window, w, h)` is `pack()` followed by `Math.max(...)`; it never limits the size to the screen.
- `WindowUtils.checkMinimumSize(window)` enlarges the window to its minimum size, even if that exceeds the screen.
- `WindowUtils.center(...)` keeps the top-left corner on the screen, but does not shrink or move a window that is too
  large.
- `UIStateUtils.uistatemanage(...)` restores saved bounds and only re-centres a window that is *completely* off screen.
  A size saved at 100 % is restored unchanged at 200 %.

Java itself already applies the Windows display scaling (since Java 9; JAuswertung does not disable it with
`sun.java2d.uiScale`). Sizes in code therefore must not be scaled again; what is missing is a limit to the screen.

## Proposal

### `safePack(Window window)`

Add to `WindowUtils`: `pack()` and then make sure the window is completely on the screen.

1. `window.pack()`.
2. Determine the usable area of the screen the window is on: `GraphicsConfiguration.getBounds()` minus
   `Toolkit.getScreenInsets(gc)` (taskbar, docks). With several screens, use the screen that contains most of the
   window; for a window not yet shown, use the parent's screen or the default screen.
3. If the window is wider or taller than the usable area, reduce its size to the usable area. Also lower the minimum
   size if it exceeds the usable area, otherwise Swing enlarges the window again.
4. Move the window so that it lies completely inside the usable area (right/bottom edge first, then left/top edge, so
   the title bar always stays reachable).

The steps 2–4 should be available on their own as well (e.g. `fitToScreen(Window)`), because they are also needed
after setting or restoring a size (see below).

## Related changes in jutils

- `WindowUtils.setSize(window, w, h)`: call `fitToScreen` at the end.
- `WindowUtils.checkMinimumSize(window)`: never enlarge beyond the usable screen area; call `fitToScreen` at the end.
- `WindowUtils.center(...)`: call `fitToScreen` after positioning.
- `UIStateUtils.uistatemanage(...)`: call `fitToScreen` after restoring the saved bounds, so sizes saved at a lower
  scaling (or on a larger screen) are reduced.
- `WizardOptionPage`: put the radio buttons into a scroll pane; with many options (16 in the JAuswertung export
  wizard) the page is taller than 515 px, and the export wizard is not resizable.

## Acceptance criteria

- On a 1920×1080 screen at 200 % (usable ≈960×515), every window that uses `safePack`, `WindowUtils.setSize`,
  `checkMinimumSize`, `center` or `uistatemanage` opens completely on screen; title bar and buttons are reachable.
- A window whose minimum size is larger than the screen can still be shown and resized on the screen.
- At 100 % on a large screen, windows open with the same sizes as today.
- A size saved at 100 % and restored at 200 % is reduced to the screen.
- Multi-monitor: a window is fitted to the screen it is shown on, not the primary screen.

## Usage in JAuswertung (after the jutils release)

| Window | Today | Change |
|---|---|---|
| `JStatisticsFrame`, `JMissingInputFrame`, `JKampfrichterManager` | at least 800×600 | `WindowUtils.setSize(this, 1000, 700)` |
| `JSelectionFrame` | minimum 800×600 | remove minimum; `WindowUtils.setSize(this, 1000, 700)` |
| `JUrkundenEditor` | 800×600 | `WindowUtils.setSize(this, 1000, 750)` |
| `JPropertiesDialog` | at least 650×800 | `WindowUtils.setSize(this, 650, 0)`; tabs in scroll panes |
| `JAKsEditor` | height fixed 700 | `WindowUtils.setSize(this, 600, 0)`; scroll panes for unscrolled tabs |
| `JStarterEditor` | minimum 400×600 | minimum 400×300; `safePack(this)` |
| `JBugReport` | 500×600 at 100,100 | `safePack(this)`; centred |
| Import wizard (`SelectionPage` 900×500) | too tall at 166 % | table 800×350; `safePack(this)` |
