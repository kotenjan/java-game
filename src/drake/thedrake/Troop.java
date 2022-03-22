package drake.thedrake;

import java.io.PrintWriter;
import java.util.List;

/**
 * bude představovat bojovou jednotku v naší hře. Každá jednotka má svoje jméno (například "Archer") a takzvaný lícový pivot a rubový pivot.
 */
public class Troop implements JSONSerializable {
    private final String name;
    private final Offset2D aversPivot;
    private final Offset2D reversPivot;
    private List<TroopAction> aversActions;
    private List<TroopAction> reversActions;

    // Hlavní konstruktor
    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversActions, List<TroopAction> reversActions){
        this.name=name;
        this.aversPivot=aversPivot;
        this.reversPivot=reversPivot;
        this.aversActions=aversActions;
        this.reversActions=reversActions;
    }

    // Konstruktor, který nastavuje oba pivoty na stejnou hodnotu
    public Troop(String name, Offset2D pivot, List<TroopAction> aversActions, List<TroopAction> reversActions){
        this(name, pivot, pivot,aversActions, reversActions);
    }

    // Konstruktor, který nastavuje oba pivoty na hodnotu [1, 1]
    public Troop(String name, List<TroopAction> aversActions, List<TroopAction> reversActions){
        this(name, new Offset2D(1,1), new Offset2D(1,1), aversActions, reversActions);
    }

    //Vrací seznam akcí pro zadanou stranu jednotky
    public List<TroopAction> actions(TroopFace face){
        if(face==TroopFace.AVERS) return aversActions;
        else return reversActions;
    }

    // Vrací jméno jednotky
    public String name(){
        return name;
    }

    // Vrací pivot na zadané straně jednotky
    public Offset2D pivot(TroopFace face){
        if(face == TroopFace.AVERS) return aversPivot;
        else return reversPivot;
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("\"" + name() + "\"");
    }
}
