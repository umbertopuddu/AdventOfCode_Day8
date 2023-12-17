import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Advent8 {
    public static void main(String[] args) {
        String[] table = {};
        char[] directions = {};
        String[] node, left, right;

        try {
            table = fileToArray("puzzle.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        PathPool stepOne = new PathPool(table);
        stepOne.addPath("AAA");

//Part1
        System.out.println("Part 1 - " + stepOne.totalSteps(true));

        PathPool stepTwo = new PathPool(table);
        for(int i = 0; i < stepTwo.node.length; i++){
            if(stepTwo.node[i].substring(2).equals("A")){
                stepTwo.addPath(stepTwo.node[i]);
            }
        }

//Part 2
        System.out.println("Part 2 - " + stepTwo.totalSteps(false));

    }



    private static String[] fileToArray(String filePath) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(filePath)));
        return text.split("\n");
    }

}

class PathPool{

    private String[] table = {};
    private char[] directions = {};
    String[] node, left, right;
    private ArrayList<Path> paths = new ArrayList<Path>();

    public PathPool(String[] lines){
        this.table = part(2, lines);
        this.directions = lines[0].toCharArray();
        this.node = initNode(lines);
        this.left = initLeft(lines);
        this.right = initRight(lines);
    }

    public void addPath(String start){
        paths.add(new Path(start));
    }

    public long totalSteps(boolean partOne){ //Use boolean true if using for part one and false for part 2
        if(partOne){
            while(!paths.get(0).arrived(true)){
                paths.get(0).step();
            }
            return paths.get(0).getSteps();
        }
        else{
            while(!this.condition()){
                this.step();
            }

            long[] steps = new long[paths.size()];
            for(int i = 0; i < steps.length; i++){
                steps[i] = paths.get(i).getSteps();
            }

            long lcm = findLCM(steps);
            return lcm;
        }
    }

    public static long findLCM(long[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array must not be empty or null");
        }

        long lcm = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            lcm = lcm(lcm, numbers[i]);
        }

        return lcm;
    }

    private static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }


    public void step() {
        paths.parallelStream()
                .filter(path -> !path.arrived(false))
                .forEach(Path::step);
    }

    public boolean condition(){
        for(int i = 0; i < paths.size(); i++){
            if(!paths.get(i).arrived(false)){
                return false;
            }
        }
        return true;
    }

    class Path extends Thread {
        private String start;
        private int cur;
        private long steps;
        private int ind;

        public Path(String start){
            this.start = start;
            this.cur = indexOf(start, node);
            this.steps = 0;
            this.ind = 0;
        }

        public String getStart() {
            return start;
        }

        public boolean arrived(boolean oneTwo){
            if(oneTwo){
                if(this.position().equals("ZZZ")){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                if(!this.position().substring(2).equals("Z")){
                    return false;
                }
                return true;
            }

        }

        public void step() {
            if (ind == directions.length) {
                ind = 0;
            }
            String dest = "";
            if (directions[ind] == 'R') {
                dest = right[cur];
            } else if (directions[ind] == 'L') {
                dest = left[cur];
            }
            int newIndex = indexOf(dest, node);

            if (newIndex != -1) {
                cur = newIndex;
                steps++;
                ind++;
            } else {
                System.err.println("Destination not found: " + dest);
            }
        }

        public String position(){
            return node[cur];
        }

        public long getSteps(){
            return steps;
        }
    }

    private String[] part(int num, String[] array){
        String[] newArr = new String[array.length - num];
        for(int i = num; i < array.length; i++){
            newArr[i - num] = array[i];
        }
        return newArr;
    }

    private static String[] initNode(String[] table){
        String[] node = new String[table.length - 2];
        for (int i = 2; i < table.length; i++) {
            node[i - 2] = table[i].substring(0, 3);
        }
        return node;
    }

    private static String[] initLeft(String[] table){
        String[] left = new String[table.length - 2];
        for (int i = 2; i < table.length; i++) {
            left[i - 2] = table[i].substring(7, 10);
        }
        return left;
    }

    private static String[] initRight(String[] table){
        String[] right = new String[table.length - 2];
        for (int i = 2; i < table.length; i++) {
            right[i - 2] = table[i].substring(12, 15);
        }
        return right;
    }

    private static int indexOf(String x, String[] domain) {
        for (int i = 0; i < domain.length; i++) {
            if (x.equals(domain[i])) {
                return i;
            }
        }
        return -1;
    }

}