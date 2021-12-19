# Project Description

## Development method

This is where we should plan:

- What to do;
- How to do it;
- Who should do what;
- etc...

Things we need to sort out:

- [ ] Game design
- [ ] Overall architecture
- [ ] JavaDoc comments
- [ ] This file documentation (this will probably move to `CONCEPTION.md`)

I can make diagrams and then export to `.html` or `.pdf` for the final hand in.

## Hand-in Instructions

**Date**: ==Thursday 23 December at 09:00==

**Hand in Format**:

- Source code will be an archive (`.zip`)
- A `README.md` explaining how to use the program including:
    - How to start it;
    - The controls and what they do;
    - Any other game rules / objectives;
- A `CONCEPTION.md` file explaining the overarching design decisions such as:
    - Architectural changes (with justification);
    - Any added classes / interfaces and how they fit into the architecture;
    - What functionality each component adds (for any behavior divergent from the instructions and any added
      functionality)

They should be written succinctly and informally. In `.md`, `.txt`, or `.pdf` preferably.

Add JavaDoc comments for all the functions, similar to the given examples.

For any fundamental changes (i.e. to the graphical interface) you should ask for their opinion by sending an email to
[cs107](cs107@epfl.ch).

You can add additional images that are copyright free (and reference their source in the `README.md`). They have to be
small because the hand in size is limited. Any extension that makes the files too big email [cs107](cs107@epfl.ch).

|                                                     | Points | Maximum grade out of 6 |
| :-------------------------------------------------: | :----: | :--------------------: |
| General documentation, `README.md`, `CONCEPTION.md` |   10   |          1.5           |
| Base components (step 1)                            |   35   |          3.25          |
| Unit interactions (step 2)                          |   35   |          5.0           |
| Actions and AI (step 3)                             |   20   |          6.0           |
| Extension (step 4, bonus/competition)               |   20   |        (bonus)         |

## Structure

**Steps**:

1. **Basic components**:
    - Have a player controlled *cursor* capable of moving and changing level;
    - Have *units* that can be used by the player.
2. **Unit interactions**:
    - Allow the cursor to have basic interaction with the units to move them.
3. **Actions and AI**:
    - Make the units have actions;
    - Have a basic "AI" that can act as an *opponent*;
    - Add a UI to display static information.
4. **Extensions**:
    - Add additional features and functionality.
