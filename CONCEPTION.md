- A `CONCEPTION.md` file explaining the overarching design decisions such as:
    - Architectural changes (with justification);
    - Any added classes / interfaces and how they fit into the architecture;
    - What functionality each component adds (for any behavior divergent from the instructions and any added
      functionality)

# Conception

## Architectural overview

### New classes and interfaces

What should be mentionned :
les éventuelles modifications personnelles que vous avez apportées à l'architecture proposée en les justifiant; les
classes/interfaces ajoutées et comment elles s'insèrent dans l'architecture; le comportement que vous attribuez à chacun
des composants introduits (si le composant n'est pas demandé ou s'il l'est mais que son comportement est une petite
variante de celui suggéré dans l'énoncé).

Part One:
-Explain our conception of ICWarsActor :
-the fact we deciceed to use an enumeration for Faction -our leaveArea/EnterArea method -etc

-Explain our conception of units for part 1:
-the abstract Unit class that we deciced to put abstract because there can t be an instance of the object -explain the
methods we decided to put in unit because they are both inhereted by Tank and Soldier(so that we avoid the code
repetition)
-explain the way we created the constructors of unit/Soldier/Tank -if you see sth else that seems important to talk
about for the part 2.2.1 -explain how we get the right name for the unit's sprite in Tank and Soldier with the method
that checks what the faction of the unit is -etc

-Initiation of levels:
-explain how the units are given to the player when he enters the game -explain how you determined their spawn
position (all the bonus things you created with the hashmap)

-Explain our conception of the ICWarsPlayer for part 1:
-maybe we should put it as abstract and explain why or why not -how the player's unit are initiated ( with the list )
and how there are registered in the area ( RegisterUnitsAsActors method)
-how in the update method the dead units are eliminated : created the isDead/isAlive mthod in the units + need to remove
the units both from the player's unit list and from the area -the leaveArea and enterArea method -how we can center the
camera on the player -how we get the right sprite name depending on the faction (same thing as for the units) (in the
instruction it was said we should to this in realPlayer but we did it here because it is the same thing for the
AIPlayer)
-what we did for checking if a player was defeated -etc

-Explain our conception of the RealPlayer for part 1:
-how we make him move -etc

-Explain our conception of ICWars for part 1:
-the interesting things we did for part 1 -how the N keyboard functionnality works with also an explanation of the
method nextArea()
-same for R

-Explain the graphic infos for part 1:
-how we create the units range -How the player can select a unit (just say that it is in the later part that we really
implement this with the interactions between a player and a unit)
-how the player can draw the path of the unit thanks to his PlayerGUI -what we did for when the U button is pressed -how
we respected the constraints for this part (see part 2.4 of the instructions) and check if the way the GUI receives the
selected unit is great and explain why we did it like this
    
    
    
    
    
    
    
    
    
    
    
    
    
