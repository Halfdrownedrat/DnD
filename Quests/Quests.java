public class Quests {

// Single actions
static String[] military = {"defend", "protect", "destroy", "huntDown", "kill"};
static String[] intell = {"solve", "investigate", "convince", "uncover", "map", "gatherIntel", "negotiate", "find"};
static String[] logieRuns = {"retrieve", "deliver", "collect"};
static String[] other = {"train"};
// Actions
static String[] actions = concatenateArrays(military, intell, logieRuns, other);

// Locations single
static String[] urban = {"city", "downtown", "skyscraper", "apartment", "suburb", "marketplace"};
static String[] farm = {"village", "forest", "countryside", "wilderness", "field"};
static String[] militaryLocations = {"base", "barracks", "outpost", "checkpoint", "command center", "military camp"};
static String[] industrial = {"factory", "warehouse", "power plant", "refinery", "dockyard"};
static String[] natural = {"mountain", "river", "lake", "cave", "desert", "jungle"};
static String[] scientificResearch = {"laboratory", "observatory", "research station"};
static String[] transport = {"airport", "seaport", "train station", "highway", "tunnel"};
static String[] underground = {"cavern", "bunker", "sewer system", "underground facility"};
// Locations
static String[] locations = concatenateArrays(urban, farm, militaryLocations, industrial, natural, scientificResearch, transport, underground);

// Rewards single
static String[] monetary = {"cash", "bonus", "paycheck", "bribe", "funds"};
static String[] material = {"weapon", "armor", "equipment", "supplies", "vehicle"};
static String[] experienceSkill = {"level-up", "training", "specialization", "new ability", "proficiency"};
static String[] intangible = {"reputation", "influence", "fame", "honor", "prestige"};
static String[] information = {"intel", "documents", "data", "blueprints", "maps"};
static String[] social = {"ally", "favor", "support", "partnership"};
static String[] access = {"key", "pass", "permit", "clearance"};
static String[] personal = {"treasure", "artifact", "rare item", "gift"};
// Rewards
static String[] rewards = concatenateArrays(monetary, material, experienceSkill, intangible, information, social, access, personal);


    public static void main(String[] args) {
        System.out.println("Quests");
        if (args.length == 0 || args.length >1 ) {
            System.out.println("No arguments provided");
        }else{
            int amount = Integer.parseInt(args[0]);
            for (int i = 0; i < amount; i++) {
                CreateQuest();              
            }
        }
    }
    public static String[] concatenateArrays(String[]... arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int pos = 0;
        for (String[] array : arrays) {
            for (String element : array) {
                result[pos++] = element;
            }
        }
        return result;
    }
    public static void CreateQuest(){
        String action = actions[(int) (Math.random() * actions.length)];
        String loc = locations[(int) (Math.random() * locations.length)];
        String rew = rewards[(int) (Math.random() * rewards.length)];

        System.out.println("Your quest is to " + action + " something at the location: " + loc + " The Reward is: " + rew);
    }
}
