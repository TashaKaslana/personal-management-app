# NoteRenderer

`NoteRenderer.kt` owns the note body model, its JSON persistence, and both the editor and list preview. The editor screen only supplies state and callbacks; the ViewModel mutates the tree.

## Model

`NoteBlock` is a sealed tree. Every block has a globally unique 8-char id (`newNoteBlockId()`).

| Type | Role |
| --- | --- |
| `Text` | Plain field: `text`, `heading`, `spans` |
| `Checkbox` | Same, plus `checked` / `label` |
| `Image` | `uri` only |
| `ModelBox` | A panel. Serial name stays `ModelBox`. |

A panel is not a title+body pair. It is a container:

- `title` / `titleHeading` / `titleSpans` — the panel's own heading field
- `type` — visual chrome. Serialized as `panelType` because `type` is the sealed-class discriminator
- `backgroundColor` — `null` means inherit the parent background
- `blocks` — nested `NoteBlock`s. Panels may contain panels

`NotePanelType`:

- `Inherit` — no chrome. Transparent only when `backgroundColor == null` (`isTransparent`)
- `Plain` — rounded box, hairline border
- `Callout` — primary left accent bar
- `Quote` — gray left accent bar, italic child text

`effectiveBackground(parent)` is `backgroundColor ?: parent`. Text color is `noteContentColor` of that result, so a dark panel background flips child text to white.

Tree helpers: `findBlock`, `containsBlock`, `flattenBlocks` (depth-first: panel, then its children). The ViewModel uses these so a block id can be edited whether it is top-level or nested.

## Persistence

`NoteContentDocument` version **2**: `{ version, titleSpans, blocks }`.

- `saveNoteContent` / `loadNoteContent` / `loadTitleSpans`
- Blank content becomes one empty `Text` block
- Non-JSON content becomes one `Text` block holding the raw string
- `ignoreUnknownKeys = true`
- No migration of the old title/body `ModelBox`. That shape will not round-trip

## How the editor renders

`NoteRenderer` walks a list and dispatches each block. For a panel it calls `NotePanel`, then **calls itself** with `block.blocks`.

What is passed down into a nested call:

- `textStyle` — resolved panel style (color, quote italic). Headings are applied per field via `withHeading`
- `parentBackground` — the panel's effective background, so a nested panel can inherit it
- the same edit callbacks. Routing is by block id, not by depth

`NotePanel` always shows a title field (`fieldKey = id + ":title"`). Child content is the `content` lambda. `pendingPanelFocusId` makes exactly that panel's title request focus once, then `onPanelFocusConsumed` clears the token. `FocusRequester` is remembered by panel id so list inserts do not steal focus from the wrong field.

`NoteImageBlock` decodes a file path (`/…`) or a content URI. Failure shows the uri as text.

## Preview

`NoteContentPreview` is the read-only card on `NoteScreen`. It takes the first `maxBlocks` top-level blocks. A panel preview shows its title plus at most 2 children, and only one nesting level (`depth < 1`). Deeper panels still exist in the document; the grid card just stops drawing them.

## How editing uses it

Wired from `NoteEditScreen` into `NoteEditViewModel`.

- Typing a text/checkbox/panel title sets `caret` (`NoteStyleTarget`) and patches that block by id, including inside panels (`updateBlock` walks the tree)
- Add menu (`Panel`, checkbox, image) inserts **after the caret block**. If the caret is inside a panel, the new block goes into that panel. Title caret (note id, not a block id) appends at the end
- A new panel sets `pendingPanelFocusId` so its title is focused
- Palette: caret in a panel edits that panel's background (`null` = inherit). Otherwise it edits the note background
- Note menu "Panel type" appears only while the caret is in a panel. `focusedPanelId()` is the **innermost** panel containing the caret

Checkbox show/hide also walks into panels: text lines become checkboxes and the reverse.

## Debugging notes

- Style toolbar targets `caret`, not the visually focused field. If bold/heading hits the wrong block, check which `onEdit` last wrote `caret`
- Panel background not applied: `backgroundColor == null` and type `Inherit` is transparent on purpose
- JSON encode crash on a new panel field named `type`: rename the JSON key with `@SerialName`, do not reuse the discriminator
- Insert lands at the end: caret `blockId` did not match any block (usually the note title)
- Preview looks flatter than the editor: depth and `maxBlocks` caps, not missing data
