package ch.epfl.cs107.play.game.icwars.area.level;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.units.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.units.Tank;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class Level1 extends ICWarsArea {
    /**
     * TODO
     */
    public Level1() {
        super(
            new SpawnPoints(Arrays.stream(new DiscreteCoordinates[]{
                new DiscreteCoordinates(2, 5),
                new DiscreteCoordinates(3, 5),
            }).collect(Collectors.toList())),
            new SpawnPoints(Arrays.stream(new DiscreteCoordinates[]{
                new DiscreteCoordinates(3, 8),
                new DiscreteCoordinates(9, 5),
            }).collect(Collectors.toList()))
        );
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public String getTitle() {
        return "icwars/Level1";
    }

    /**
     * TODO
     */
    protected void createArea() {
        // Base
        registerActor(new Background(this));
    }


    public List<Unit> factionUnits(ICWarsActor.Faction faction) {
        return switch (faction) {
            case ENEMY -> new ArrayList<>(List.of(new Unit[]{
                new Tank(
                    this,
                    this.getFreeAllySpawnPosition().orElse(new DiscreteCoordinates(0, 0)),
                    ICWarsActor.Faction.ALLY
                ),
                new Soldier(
                    this,
                    this.getFreeAllySpawnPosition().orElse(new DiscreteCoordinates(0, 0)),
                    ICWarsActor.Faction.ALLY
                )})
            );
            case ALLY -> new ArrayList<>(List.of(new Unit[]{
                new Tank(
                    this,
                    this.getFreeEnemySpawnPosition().orElse(new DiscreteCoordinates(0, 0)),
                    ICWarsActor.Faction.ENEMY
                ),
                new Soldier(
                    this,
                    this.getFreeEnemySpawnPosition().orElse(new DiscreteCoordinates(0, 0)),
                    ICWarsActor.Faction.ENEMY
                )})
            );
        };
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public DiscreteCoordinates getAllyCenter() {
        return new DiscreteCoordinates(0, 0);
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public DiscreteCoordinates getEnemyCenter() {
        return new DiscreteCoordinates(7, 4);
    }
}
