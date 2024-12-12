import java.util.Random;

public class Creature {

    private final String name;
    private final String creatureType;
    private final int strength;
    private final int dexterity;
    private final int constitution;
    private final int intelligence;
    private final int wisdom;
    private final int charisma;
    private final int armorClass;

    // Humanoid specific attributes
    private String faction;
    private String gender;
    private String sex;
    private int age;
    private double height;


    // Attitude
    private String friendliness;

    public Creature(String name, String creatureType) {
        this.name = name;
        this.creatureType = creatureType;
        Random rand = new Random();
        this.strength = rand.nextInt(9) - 4;
        this.dexterity = rand.nextInt(9) - 4;
        this.constitution = rand.nextInt(9) - 4;
        this.intelligence = rand.nextInt(9) - 4;
        this.wisdom = rand.nextInt(9) - 4;
        this.charisma = rand.nextInt(9) - 4;
        this.armorClass = 10;

        if(this.creatureType.equals("human")){
            this.faction = "random";
            this.gender = "male";
            this.sex = this.gender;
            this.age = rand.nextInt(40) + 10;
            this.height = rand.nextInt(50) + 150;
        }
    }
    
    public String printValues() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creature{");
        sb.append("name='").append(name).append('\'');
        sb.append(", creatureType='").append(creatureType).append('\'');
        sb.append(", strength=").append(strength);
        sb.append(", dexterity=").append(dexterity);
        sb.append(", constitution=").append(constitution);
        sb.append(", intelligence=").append(intelligence);
        sb.append(", wisdom=").append(wisdom);
        sb.append(", charisma=").append(charisma);
        sb.append(", armorClass=").append(armorClass);

        if (creatureType.equals("human")) {
            sb.append(", faction='").append(faction).append('\'');
            sb.append(", gender='").append(gender).append('\'');
            sb.append(", sex='").append(sex).append('\'');
            sb.append(", age=").append(age);
            sb.append(", height=").append(height);
        }

        sb.append('}');
        return sb.toString();
    }
    
}