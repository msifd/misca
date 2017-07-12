package ru.ariadna.misca.crabs.combat.parts;

import java.io.Serializable;

/**
 * Действие персонажа и часть тела, куда он целится.
 */
public class Action implements Serializable {
    public ActionType type;
    public BodyPartType bodyPart;

//    public Action(ActionType type, BodyPartType bodyPart) {
//        this.type = type;
//        this.bodyPart = bodyPart;
//    }
}
