package com.github.ptran779.aegisops;

import com.github.ptran779.aegisops.entity.extra.FallingHellPod;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.api.IEntityTeam;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import static com.github.ptran779.aegisops.server.EntityInit.*;

public class Utils {
  private static final List<String> MALE_FN = List.of("James", "Michael", "John", "Robert", "David", "William", "Richard", "Joseph", "Thomas", "Christopher", "Charles", "Daniel", "Matthew", "Anthony", "Mark", "Steven", "Donald", "Andrew", "Joshua", "Paul", "Kenneth", "Kevin", "Brian", "Timothy", "Ronald", "Jason", "George", "Edward", "Jeffrey", "Ryan", "Jacob", "Nicholas", "Gary", "Eric", "Jonathan", "Stephen", "Larry", "Justin", "Benjamin", "Scott", "Brandon", "Samuel", "Gregory", "Alexander", "Patrick", "Frank", "Jack", "Raymond", "Dennis", "Tyler", "Aaron", "Jerry", "Jose", "Nathan", "Adam", "Henry", "Zachary", "Douglas", "Peter", "Noah", "Kyle", "Ethan", "Christian", "Jeremy", "Keith", "Austin", "Sean", "Roger", "Terry", "Walter", "Dylan", "Gerald", "Carl", "Jordan", "Bryan", "Gabriel", "Jesse", "Harold", "Lawrence", "Logan", "Arthur", "Bruce", "Billy", "Elijah", "Joe", "Alan", "Juan", "Liam", "Willie", "Mason", "Albert", "Randy", "Wayne", "Vincent", "Lucas", "Caleb", "Luke", "Bobby", "Isaac", "Bradley");
  private static final List<String> FEMALE_FN = List.of("Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Karen", "Sarah", "Lisa", "Nancy", "Sandra", "Ashley", "Emily", "Kimberly", "Betty", "Margaret", "Donna", "Michelle", "Carol", "Amanda", "Melissa", "Deborah", "Stephanie", "Rebecca", "Sharon", "Laura", "Cynthia", "Amy", "Kathleen", "Angela", "Dorothy", "Shirley", "Emma", "Brenda", "Nicole", "Pamela", "Samantha", "Anna", "Katherine", "Christine", "Debra", "Rachel", "Olivia", "Carolyn", "Maria", "Janet", "Heather", "Diane", "Catherine", "Julie", "Victoria", "Helen", "Joyce", "Lauren", "Kelly", "Christina", "Joan", "Judith", "Ruth", "Hannah", "Evelyn", "Andrea", "Virginia", "Megan", "Cheryl", "Jacqueline", "Madison", "Sophia", "Abigail", "Teresa", "Isabella", "Sara", "Janice", "Martha", "Gloria", "Kathryn", "Ann", "Charlotte", "Judy", "Amber", "Julia", "Grace", "Denise", "Danielle", "Natalie", "Alice", "Marilyn", "Diana", "Beverly", "Jean", "Brittany", "Theresa", "Frances", "Kayla", "Alexis", "Tiffany", "Lori", "Kathy");
  private static final List<String> LN = List.of("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzales", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts", "Gomez", "Phillips", "Evans", "Turner", "Diaz", "Parker", "Cruz", "Edwards", "Collins", "Reyes", "Stewart", "Morris", "Morales", "Murphy", "Cook", "Rogers", "Gutierrez", "Ortiz", "Morgan", "Cooper", "Peterson", "Bailey", "Reed", "Kelly", "Howard", "Ramos", "Kim", "Cox", "Ward", "Richardson", "Watson", "Brooks", "Chavez", "Wood", "James", "Bennet", "Gray", "Mendoza", "Ruiz", "Hughes", "Price", "Alvarez", "Castillo", "Sanders", "Patel", "Myers", "Long", "Ross", "Foster", "Jimenez");

  private static final List<EntityType<? extends AbstractAgentEntity>> AGENT_POOL = List.of(
    SOLDIER.get(),
    SNIPER.get(),
    HEAVY.get(),
    DEMOLITION.get(),
    MEDIC.get(),
    ENGINEER.get(),
    SWORDMAN.get()
  );

  public static AbstractAgentEntity getRandomAgent(Level level) {return AGENT_POOL.get(level.random.nextInt(AGENT_POOL.size())).create(level);}

  public static BlockPos findSolidGroundBelow(BlockPos start, Level level) {
    BlockPos.MutableBlockPos pos = start.mutable();
    while (pos.getY() > level.getMinBuildHeight()) {
      BlockState state = level.getBlockState(pos);
      if (!state.isAir()) {return pos.immutable();}
      pos.move(Direction.DOWN);
    }
    return null; // No ground found (shouldn't happen)
  }

  public static String randomName(boolean isFemale) {
    String first = isFemale
        ? FEMALE_FN.get(ThreadLocalRandom.current().nextInt(FEMALE_FN.size()))
        : MALE_FN.get(ThreadLocalRandom.current().nextInt(MALE_FN.size()));
    String last = LN.get(ThreadLocalRandom.current().nextInt(LN.size()));
    return first + " " + last;
  }

  public static void summonReinforcement(double x, double y, double z, ServerLevel level){
    // generate random trajectory, and perform squad spawning
    float xRandTraj = (level.random.nextFloat()-0.5f) * 0.025f;
    float zRandTraj = (level.random.nextFloat()-0.5f) * 0.025f;
    FallingHellPod pod = new FallingHellPod(FALLING_HELL_POD.get(), level);
    pod.setDrift(xRandTraj, zRandTraj);
    pod.setPos(x, y, z);
    level.addFreshEntity(pod);
    // Spawn agent
    AbstractAgentEntity agent = getRandomAgent(level);
    agent.initCosmetic();
    level.addFreshEntity(agent);
    //agent ride pod
    agent.startRiding(pod, true); // force = true in case agent is riding something else
  }

  public enum TargetMode {
    OFF,                        // Do not scan or target
    HOSTILE_ONLY,               // Hostile mobs only
    ENEMY_AGENTS,  // Hostile mobs + agents not on owner's team
    ALL;         // Anything not on owner's team

    public static final TargetMode[] VALUES = values();
    public static TargetMode fromId(int id) {
      return VALUES[id];
    }
    public static TargetMode nextTargetMode(int id) {
      int next = (id + 1) % VALUES.length;
      return VALUES[next];
    }
  }

  public enum FollowMode {
    WANDER,     // Move around randomly
    STAY,       // Stay in place
    FOLLOW;     // Follow the owner/player

    public static final FollowMode[] VALUES = values();

    public static FollowMode fromId(int id) {
      return VALUES[id];
    }

    public static FollowMode nextFollowMode(int id) {
      int next = (id + 1) % VALUES.length;
      return VALUES[next];
    }
  }

  public enum AniMove {
    NORM, ATTACK, RELOAD, DISP_RELOAD, SALUTE, SPECIAL;
//    NORM, ATTACK, RELOAD, DISP_RELOAD, SALUTE, SPECIAL0, SPECIAL1, SPECIAL2, SPECIAL3;

    public static final AniMove[] VALUES = values();
    public static AniMove fromId(int id) {
      return VALUES[id];
    }
  }

  public static <U extends Entity, T extends Entity> T findNearestEntity(U user, Class<T> type, double radius, Predicate<T> func) {
    AABB box = user.getBoundingBox().inflate(radius);
    List<T> entities = user.level().getEntitiesOfClass(type, box, func);

    return entities.stream()
        .min(Comparator.comparingDouble(user::distanceToSqr))
        .orElse(null);
  }

  public static boolean hasFriendlyInLineOfFire(Mob user, LivingEntity target) {
    if (!(user instanceof IEntityTeam userTeam)) return false;
    Vec3 start = user.getEyePosition();
    Vec3 end = target.getEyePosition();

    AABB pathAABB = new AABB(start, end).inflate(0.5); // widen slightly for tall hitbox
    List<LivingEntity> teammates = user.level().getEntitiesOfClass(
        LivingEntity.class,pathAABB, other ->
            other instanceof IEntityTeam otherTeam &&
            userTeam.isFriendlyMod(otherTeam, other.level()) &&
            other.getBoundingBox().clip(start, end).isPresent()
    );
    return !teammates.isEmpty();
  }

  //use for both client and server to help with setting default skin
  public static String makeSafeSkinName(String rawFileName) {
    if (rawFileName == null || rawFileName.isEmpty()) return "skin_default";

    // 1. Strip extension if present
    int dotIndex = rawFileName.lastIndexOf('.');
    String base = (dotIndex > 0 ? rawFileName.substring(0, dotIndex) : rawFileName);

    // 2. Lowercase
    base = base.toLowerCase(Locale.ROOT);

    // 3. Normalize Unicode → ASCII
    base = Normalizer.normalize(base, Normalizer.Form.NFKD)
        .replaceAll("\\p{M}", ""); // removes diacritics

    // 4. Replace illegal ResourceLocation characters with _
    base = base.replaceAll("[^a-z0-9._-]", "_");

    // 5. Collapse multiple underscores and trim edges
    base = base.replaceAll("_+", "_")
        .replaceAll("^_|_$", "");

    // 6. Fallback if empty after cleaning
    if (base.isEmpty()) base = "skin";

    // 7. Append short hash to avoid collisions
    String hash = Integer.toHexString(rawFileName.hashCode());
    return base + "_" + hash;
  }
}
