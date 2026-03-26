import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

class Block {
    String voterId;
    String candidate;
    String previousHash;
    String hash;
    long timeStamp;

    Block(String voterId, String candidate, String previousHash) {
        this.voterId = voterId;
        this.candidate = candidate;
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    String calculateHash() {
        return applySHA256(voterId + candidate + previousHash + timeStamp);
    }

    static String applySHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

public class VotingSystem {

    static ArrayList<Block> blockchain = new ArrayList<>();
    static HashSet<String> votedUsers = new HashSet<>();
    static HashMap<String, Integer> results = new HashMap<>();
    static ArrayList<String> candidates = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);
    static int totalVotes = 0;

    static void initCandidates() {
        candidates.add("A");
        candidates.add("B");
        candidates.add("C");
    }

    static void createGenesisBlock() {
        if (blockchain.size() == 0) {
            Block genesis = new Block("0", "Genesis", "0");
            blockchain.add(genesis);
        }
    }

    static void showCandidates() {
        System.out.println("\nAvailable Candidates:");
        for (String c : candidates) {
            System.out.println("- " + c);
        }
    }

    static void addVote(String voterId, String candidate) {
        String prevHash = blockchain.get(blockchain.size() - 1).hash;

        Block newBlock = new Block(voterId, candidate, prevHash);
        blockchain.add(newBlock);

        votedUsers.add(voterId);
        results.put(candidate, results.getOrDefault(candidate, 0) + 1);

        totalVotes++;

        System.out.println(" Vote added successfully!");
    }

    static void castVote() {
        System.out.print("Enter Voter ID: ");
        String voterId = sc.next();

        if (votedUsers.contains(voterId)) {
            System.out.println(" You have already voted!");
            return;
        }

        showCandidates();
        System.out.print("Enter Candidate: ");
        String candidate = sc.next();

        if (!candidates.contains(candidate)) {
            System.out.println(" Invalid candidate!");
            return;
        }

        addVote(voterId, candidate);
    }

    static void displayBlockchain() {
        if (blockchain.size() <= 1) {
            System.out.println(" No votes yet!");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        for (int i = 0; i < blockchain.size(); i++) {
            Block b = blockchain.get(i);

            System.out.println("\n------ Block " + i + " ------");
            System.out.println("Voter ID: " + b.voterId);
            System.out.println("Candidate: " + b.candidate);
            System.out.println("Time: " + sdf.format(new Date(b.timeStamp)));
            System.out.println("Hash: " + b.hash);
            System.out.println("Previous Hash: " + b.previousHash);
        }
    }

    static void validateBlockchain() {
        for (int i = 1; i < blockchain.size(); i++) {
            Block current = blockchain.get(i);
            Block previous = blockchain.get(i - 1);

            if (!current.hash.equals(current.calculateHash())) {
                System.out.println(" Blockchain is INVALID!");
                return;
            }

            if (!current.previousHash.equals(previous.hash)) {
                System.out.println(" Blockchain is BROKEN!");
                return;
            }
        }

        System.out.println(" Blockchain is VALID");
    }

    static void showResults() {
        if (results.isEmpty()) {
            System.out.println(" No votes yet!");
            return;
        }

        System.out.println("\n--- RESULTS ---");

        String winner = "";
        int maxVotes = 0;

        for (String candidate : results.keySet()) {
            int votes = results.get(candidate);
            System.out.println(candidate + " : " + votes);

            if (votes > maxVotes) {
                maxVotes = votes;
                winner = candidate;
            }
        }

        System.out.println("Total Votes: " + totalVotes);
        System.out.println(" Winner: " + winner);
    }

    static void searchVote() {
        System.out.print("Enter Voter ID to search: ");
        String id = sc.next();

        for (Block b : blockchain) {
            if (b.voterId.equals(id)) {
                System.out.println(" Vote Found: " + b.candidate);
                return;
            }
        }

        System.out.println(" No vote found!");
    }

    static void tamperBlock() {
        if (blockchain.size() <= 1) {
            System.out.println(" No blocks to tamper!");
            return;
        }

        blockchain.get(1).candidate = "HACKED";
        System.out.println(" Block tampered!");
    }

    static void showStats() {
        System.out.println("\n--- SYSTEM STATS ---");
        System.out.println("Total Blocks: " + blockchain.size());
        System.out.println("Total Votes: " + totalVotes);
        System.out.println("Total Candidates: " + candidates.size());
    }

    public static void main(String[] args) {

        initCandidates();
        createGenesisBlock();

        while (true) {
            System.out.println("\n==== BLOCKCHAIN VOTING SYSTEM ====");
            System.out.println("1. Cast Vote");
            System.out.println("2. View Blockchain");
            System.out.println("3. Validate Blockchain");
            System.out.println("4. Show Results");
            System.out.println("5. Search Vote");
            System.out.println("6. System Stats");
            System.out.println("7. Tamper Blockchain");
            System.out.println("8. Exit");

            System.out.print("Enter choice: ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1: castVote(); break;
                case 2: displayBlockchain(); break;
                case 3: validateBlockchain(); break;
                case 4: showResults(); break;
                case 5: searchVote(); break;
                case 6: showStats(); break;
                case 7: tamperBlock(); break;
                case 8: System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }
}