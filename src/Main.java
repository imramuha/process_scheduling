/*
*
*  author:  Imran Muhammad
*  date:    01/03/2021
*  files:   Main & LineChart (for chart)
*
* */

// Imports
import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jfree.ui.RefineryUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// *************************
// once we have the needed data we can start with the following algorithms
// 01. FCFS [DONE]
// 02. SJF [DONE]
// 03. SRT
// 04. RR q = 2 [DONE]
// 04. RR q = 4 [DONE]
// 04. RR q = 8 [DONE]
// 05. HRRN [DONE]
// 06. Multilevel feedback mode - 5 queues & timeslice zelf te bepalen en motiveer
// 07. Multilevel feedback mode - 5 queues & timeslice zelf te bepalen

// once we have ran our algorithms we have to find a way to compare/evaluate
// our data, pref in a graph or something
// 4 graphs for 10000 and 20000 processes


class Main {
    public static void main(String[] args) throws Exception {

        // reading xml-file and making the data usable
        File xmlFile = new File("processen5.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.parse(xmlFile);

        NodeList list = document.getElementsByTagName("process");

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < list.getLength(); i++) {

            Node node = list.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                Process process = new Process(
                        Integer.parseInt(element.getElementsByTagName("pid").item(0).getTextContent()),
                        Integer.parseInt(element.getElementsByTagName("arrivaltime").item(0).getTextContent()),
                        Integer.parseInt(element.getElementsByTagName("servicetime").item(0).getTextContent())
                );
                processes.add(process);
            }
        }

        // lists of our accessible data
        int amountProcesses = processes.size();

        // 01. FCFS
        List<Float> ntatPercentilesFCFS = new ArrayList<>();
        List<Float> wtPercentilesFCFS = new ArrayList<>();
        //FCFS.run(processes, amountProcesses, ntatPercentilesFCFS, wtPercentilesFCFS);

        // 02. SJF (send servicetimes sorted by high->low)
        List<Float> ntatPercentilesSJF = new ArrayList<>();
        List<Float> wtPercentilesSJF = new ArrayList<>();
        //SJF.run(processes, amountProcesses, ntatPercentilesSJF, wtPercentilesSJF);

        // 03. SRT
        List<Float> ntatPercentilesSRT = new ArrayList<>();
        List<Float> wtPercentilesSRT = new ArrayList<>();
        SRT.run(processes, ntatPercentilesSRT, wtPercentilesSRT);

        // 04-06. RR met verschillende q's = 2, 4 en 8.
        int q2 = 2;
        int q4 = 4;
        int q8 = 8;
        List<Float> ntatPercentilesRR2 = new ArrayList<>();
        List<Float> wtPercentilesRR2 = new ArrayList<>();
        //RR.run(processes, amountProcesses, ntatPercentilesRR2, wtPercentilesRR2, q2);
        //RR.run(processes, amountProcesses, ntatPercentilesRR2, wtPercentilesRR2, q4);
        //RR.run(processes, amountProcesses, ntatPercentilesRR2, wtPercentilesRR2, q8);

        // 07. HRRN
        List<Float> ntatPercentilesHRRN = new ArrayList<>();
        List<Float> wtPercentilesHRRN = new ArrayList<>();
        //HRRN.run(processes, amountProcesses, ntatPercentilesHRRN, wtPercentilesHRRN);

        // 08-09. Multilevel Feedback met 2 verschillende queues
        List<Float> ntatPercentilesMLFB = new ArrayList<>();
        List<Float> wtPercentilesMLFB = new ArrayList<>();
        //MLFB.run(processes, amountProcesses, ntatPercentilesMLFB, wtPercentilesMLFB);

        // get our ST percentiles for the x-axis
        List<Integer> stPercentiles = new ArrayList<Integer>();
        //stPercentile20k(processes, amountProcesses, stPercentiles);

        // This is where we consume our data to create line charts through LineChart.java (jFreeChart library)
        // NTAT vs ST chart
        /*LineChart_AWT chart = new LineChart_AWT( "NTAT vs ST","Normalized Turnaround time vs Service Time", stPercentiles, ntatPercentilesFCFS, ntatPercentilesSJF);

        // WT vs ST chart
        //LineChart_AWT chart = new LineChart_AWT( "WT vs ST","Waiting times vs Service Time", stPercentiles, wtPercentilesFCFS, wtPercentilesSJF);
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );*/

    }

    public static class Process implements Comparable<Process> {

        private int pid;
        private int arrivaltime;
        private int servicetime;
        private int completionTime;
        private boolean completed;
        private int serviceTimeLeft;
        private int clockCompleted;
        private int status;
        private int startTime;

        public Process(int pid, int arrivaltime, int servicetime) {
            this.pid = pid;
            this.arrivaltime = arrivaltime;
            this.servicetime = servicetime;
            this.completed = false;
            this.clockCompleted = 0;
            this.serviceTimeLeft = servicetime;
        }

        public int getPid() {
            return pid;
        }

        public int getArrivaltime() {
            return arrivaltime;
        }

        public int getServicetime() {
            return servicetime;
        }

        public void oneClock(){
            if(serviceTimeLeft == 0){
                //Should never happen
                System.out.println("OneClock command thrown with no service time left");
            }
            else {
                this.serviceTimeLeft -= 1;
                if(this.serviceTimeLeft == 0){
                    this.completed = true;
                }
            }
        }

        public int getStartTime(){
            return this.startTime;
        }

        public void setStartTime(int clock){
            this.startTime = clock;
        }

        public boolean getCompleted(){
            return this.completed;
        }

        public void setCompleted(){
            this.completed = true;
        }

        public int getServiceTimeLeft(){
            return this.serviceTimeLeft;
        }

        public int getClockCompleted(){
            return this.clockCompleted;
        }

        public void setClockCompleted(int clock){
            this.clockCompleted = clock;
        }

        public void reset(){
            this.completed = false;
            this.serviceTimeLeft = this.servicetime;
            this.clockCompleted = 0;
        }

        @Override
        public int compareTo(Process o) {
            return ((Integer)this.getServicetime()).compareTo(o.getServicetime());
        }

        public int compareToAT(Process o) {
            return ((Integer)this.getArrivaltime()).compareTo(o.getArrivaltime());
        }


        public int compareToSTLeft(Process o){
            return ((Integer)this.getServiceTimeLeft()).compareTo(o.getServiceTimeLeft());
        }
    }

    // Function to calculate percentile
    static void stPercentile20k(List<Main.Process> processes, int amountProcesses, List<Integer> stPercentile) {

        // sort our processes by shortestservicetimes
        Collections.sort(processes, Process::compareTo);

        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + processes.get(counter).getServicetime();

                counter++;
            }
            stPercentile.add((tempPercentile / (amountProcesses / 100)));
        }
    }
}

// first come first serve
class FCFS {

    // Function to find the waiting time for all
    static void findWaitingtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes) {

        int[] currentServicetime = new int[amountProcesses];
        // starts at 0
        waitingtimes[0] = 0;
        currentServicetime[0] = 0;

        // calculating waiting time and save in a list
        for(int i = 1; i < amountProcesses; i++) {

            // Add burst time of previous processes
            //representing wasted time in queue
            int wasted = 0;

            // Add burst time of previous processes
            currentServicetime[i] = currentServicetime[i-1] + processes.get(i-1).getServicetime();

            // Find waiting time for current process =
            // sum - at[i]
            waitingtimes[i] = currentServicetime[i] - processes.get(i).getArrivaltime();

            // if negative, already in queue
            // wasted time is basically time for process to wait after a process is over
            if (waitingtimes[i] < 0) {
                wasted = Math.abs(waitingtimes[i]);
                waitingtimes[i] = 0;
            }
            //Add wasted time
            currentServicetime[i] = currentServicetime[i] + wasted;
        }
    }

    // Function to calculate turn around time
    static void findTurnaroundtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes, int[] turnaroundtimes) {

        // Calculating turnaround time by adding servicetimes and waitingtimes
        for (int i = 0; i < amountProcesses ; i++) {
            turnaroundtimes[i] = processes.get(i).getServicetime() + waitingtimes[i];
        }
    }

    // Function to calculate normalized
    static void findNormalizedTurnaroundtime(List<Main.Process> processes, int amountProcesses,  int[] turnaroundtimes, float[] normalizedTurnaroundtimes) {
        // Calculating normalized turnaround time by diving turnaroudtimes with servicetimes
        for (int i = 0; i < amountProcesses ; i++) {
            normalizedTurnaroundtimes[i] = (float) turnaroundtimes[i] / (float) processes.get(i).getServicetime();
        }
    }

    // Function to calculate percentile
    static void ntatPercentile20k(int amountProcesses, float[] normalizedTurnaroundtimes, List<Float> ntatPercentile) {

        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            float tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + normalizedTurnaroundtimes[counter];

                counter++;
            }
            ntatPercentile.add(tempPercentile / (amountProcesses / 100));
        }
    }

    // Function to calculate percentile
    static void wtPercentile20k(int amountProcesses, int[] waitingtimes, List<Float> wtPercentile) {
        int counter = 0;

        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;
            for(int j = 0; j < (amountProcesses / 100); j++) {
                tempPercentile= tempPercentile + waitingtimes[counter];
                counter++;
            }
           wtPercentile.add((float) tempPercentile / (amountProcesses / 100));
        }

    }

    //Function to calculate average time
    static void run(List<Main.Process> processes, int amountProcesses, List<Float> ntatPercentiles, List<Float> wtPercentiles) {

        // sort processes by arrivaltime
        Collections.sort(processes, Main.Process::compareToAT);

        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        int starttime;
        int endtime = 0;

        // Function to find waiting time of all processes
        findWaitingtime(processes, amountProcesses, waitingtimes);
        // Function to find turn around time for all processes
        findTurnaroundtime(processes, amountProcesses, waitingtimes, turnaroundtimes);
        // Function to find normalized turn around time
        findNormalizedTurnaroundtime(processes, amountProcesses, turnaroundtimes, normalizedTurnaroundtimes);

        // Display processes along with all details
        System.out.println("01. first come first serve");
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < amountProcesses; i++) {

            // global parameters
            totalWaitingtime = (totalWaitingtime + waitingtimes[i]);
            totalTurnaroundtime = totalTurnaroundtime + turnaroundtimes[i];
            totalNormalizedTurnaroundtime = totalNormalizedTurnaroundtime + ( (float) turnaroundtimes[i] / (float) processes.get(i).getServicetime());

            // start and end time
            starttime = 0;
            if(i != 0) {
                starttime = endtime + 1;
            }
            endtime = endtime + processes.get(i).getServicetime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", processes.get(i).getPid());
            System.out.printf(" %-13s", processes.get(i).getArrivaltime());
            System.out.printf(" %-15s", processes.get(i).getServicetime());
            System.out.printf(" %-15s", waitingtimes[i]);
            System.out.printf(" %-22s", turnaroundtimes[i]);
            System.out.printf("%.2f                     ", normalizedTurnaroundtimes[i]);
            System.out.printf("%-10s", starttime);
            System.out.printf(" %-12s%n", endtime);
        }

        // calculating the averages from the totals / amount of processes
        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);

        ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
        wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);

        System.out.println("END - first come first serve \n");
    }
}

class SJF {

    // Function to find the waiting time for all
    static void findWaitingtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes) {

        int[] currentServicetime = new int[amountProcesses];
        // starts at 0
        waitingtimes[0] = 0;
        currentServicetime[0] = 0;

        // calculating waiting time and save in a list
        for(int i = 1; i < amountProcesses; i++) {

            // Add burst time of previous processes
            //representing wasted time in queue
            int wasted = 0;

            // Add burst time of previous processes
            currentServicetime[i] = currentServicetime[i-1] + processes.get(i-1).getServicetime();

            // Find waiting time for current process =
            // sum - at[i]
            waitingtimes[i] = currentServicetime[i] - processes.get(i).getArrivaltime();

            // if negative, already in queue
            // wasted time is basically time for process to wait after a process is over
            if (waitingtimes[i] < 0) {
                wasted = Math.abs(waitingtimes[i]);
                waitingtimes[i] = 0;
            }
            //Add wasted time
            currentServicetime[i] = currentServicetime[i] + wasted;
        }
    }

    // Function to calculate turn around time
    static void findTurnaroundtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes, int[] turnaroundtimes) {

        // Calculating turnaround time by adding servicetimes and waitingtimes
        for (int i = 0; i < amountProcesses ; i++) {
            turnaroundtimes[i] = processes.get(i).getServicetime() + waitingtimes[i];
        }
    }

    // Function to calculate normalized
    static void findNormalizedTurnaroundtime(List<Main.Process> processes, int amountProcesses,  int[] turnaroundtimes, float[] normalizedTurnaroundtimes) {
        // Calculating normalized turnaround time by diving turnaroudtimes with servicetimes
        for (int i = 0; i < amountProcesses ; i++) {
            normalizedTurnaroundtimes[i] = (float) turnaroundtimes[i] / (float) processes.get(i).getServicetime();
        }
    }

    // Function to calculate percentile
    static void ntatPercentile20k(int amountProcesses, float[] normalizedTurnaroundtimes, List<Float> ntatPercentile) {

        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            float tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + normalizedTurnaroundtimes[counter];

                counter++;
            }
            ntatPercentile.add(tempPercentile / (amountProcesses / 100));
        }
    }

    // Function to calculate percentile
    static void wtPercentile20k(int amountProcesses, int[] waitingtimes, List<Float> wtPercentile) {
        int counter = 0;

        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;
            for(int j = 0; j < (amountProcesses / 100); j++) {
                tempPercentile= tempPercentile + waitingtimes[counter];
                counter++;
            }
            wtPercentile.add((float) tempPercentile / (amountProcesses / 100));
        }

    }

    //Function to calculate average time
    static void run(List<Main.Process> processes, int amountProcesses, List<Float> ntatPercentiles, List<Float> wtPercentiles) {

        // sort processes by lowest ST
        Collections.sort(processes, Main.Process::compareTo);

        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        int starttime;
        int endtime = 0;

        // Function to find waiting time of all processes
        findWaitingtime(processes, amountProcesses, waitingtimes);
        // Function to find turn around time for all processes
        findTurnaroundtime(processes, amountProcesses, waitingtimes, turnaroundtimes);
        // Function to find normalized turn around time
        findNormalizedTurnaroundtime(processes, amountProcesses, turnaroundtimes, normalizedTurnaroundtimes);

        // Display processes along with all details
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < amountProcesses; i++) {

            // global parameters
            totalWaitingtime = (totalWaitingtime + waitingtimes[i]);
            totalTurnaroundtime = totalTurnaroundtime + turnaroundtimes[i];
            totalNormalizedTurnaroundtime = totalNormalizedTurnaroundtime + ( (float) turnaroundtimes[i] / (float) processes.get(i).getServicetime());

            // start and end time
            starttime = 0;
            if(i != 0) {
                starttime = endtime + 1;
            }
            endtime = endtime + processes.get(i).getServicetime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", processes.get(i).getPid());
            System.out.printf(" %-13s", processes.get(i).getArrivaltime());
            System.out.printf(" %-15s", processes.get(i).getServicetime());
            System.out.printf(" %-15s", waitingtimes[i]);
            System.out.printf(" %-22s", turnaroundtimes[i]);
            System.out.printf("%.2f                     ", normalizedTurnaroundtimes[i]);
            System.out.printf("%-10s", starttime);
            System.out.printf(" %-12s%n", endtime);
        }

        // calculating the averages from the totals / amount of processes
        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);

        ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
        wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);
    }
}

//Shortest remaining time
class SRT {

    // Function to find the waiting time for all
    // processes
    static void sortProcesses(List<Main.Process> processes) {

        processes.sort(Main.Process::compareToAT);
        List<Main.Process> waitingProcessList = new ArrayList<>();
        Main.Process currentProcess = null;
        int clock = 0;

        // calculating waiting time and save in a list
        for (Main.Process process : processes) {

            while (clock != process.getArrivaltime()) {

                if (currentProcess != null) {
                    System.out.println("Before " + currentProcess.getPid() + " "+ currentProcess.getServiceTimeLeft()  + " clock:"+ clock);
                    if (currentProcess.getCompleted()) {
                        currentProcess.setClockCompleted(clock);
                        if (!waitingProcessList.isEmpty()) {
                            waitingProcessList.sort(Main.Process::compareToSTLeft);
                            currentProcess = waitingProcessList.get(0);
                            waitingProcessList.remove(0);
                            if (currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()) {
                                currentProcess.setStartTime(clock);
                                clock += 1;
                            }
                        }
                        else{
                            currentProcess = null;
                        }
                    }
                    else{
                        clock += 1;
                        currentProcess.oneClock();
                        System.out.println("After " + currentProcess.getPid() + " "+ currentProcess.getServiceTimeLeft() + " clock:"+ clock);

                    }
                } else {
                    clock += 1;
                    System.out.println("Only clock +1"  + " clock:"+ clock);
                }
            }
            if (currentProcess == null || currentProcess.getServiceTimeLeft() > currentProcess.getServicetime()) {
                System.out.println("Before change currentprocess" + currentProcess.getPid() + " "+ currentProcess.getServiceTimeLeft() + " clock:"+ clock);

                if (currentProcess != null) {
                    waitingProcessList.add(currentProcess);
                }
                currentProcess = process;
                if (currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()) {
                    currentProcess.setStartTime(clock);
                }
                System.out.println("After change before clock currentprocess" + currentProcess.getPid() + " "+ currentProcess.getServiceTimeLeft());

                currentProcess.oneClock();
                clock += 1;
                System.out.println("After change before clock currentprocess" + currentProcess.getPid() + " "+ currentProcess.getServiceTimeLeft());

            } else {
                waitingProcessList.add(process);
                currentProcess.oneClock();
                clock += 1;
                if (currentProcess.getCompleted()) {
                    currentProcess.setClockCompleted(clock);

                    if (!waitingProcessList.isEmpty()) {
                        Collections.sort(waitingProcessList, Main.Process::compareToSTLeft);
                        currentProcess = waitingProcessList.get(0);
                    }
                    else{
                        currentProcess = null;
                    }
                }
            }
        }
        Collections.sort(waitingProcessList, Main.Process::compareToSTLeft);
        for(Main.Process process : waitingProcessList){
            while(!process.getCompleted()){
                clock += 1;
                process.oneClock();
                if(process.getCompleted()){
                    process.setClockCompleted(clock);
                }
            }
        }
    }

    // Function to calculate percentile
    static void stPercentile20k(int amountProcesses, List<Integer> servicetimes, List<Integer> percentile) {

        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + servicetimes.get(counter);

                counter++;
            }
            percentile.add((tempPercentile / (amountProcesses / 100)));
        }
    }

    // Function to calculate percentile
    static void ntatPercentile20k(int amountProcesses, float[] normalizedTurnaroundtimes, List<Float> ntatPercentile) {

        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            float tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + normalizedTurnaroundtimes[counter];

                counter++;
            }
            ntatPercentile.add(tempPercentile / (amountProcesses / 100));
        }
    }

    // Function to calculate percentile
    static void wtPercentile20k(int amountProcesses, List<Integer> waitingtime, List<Float> wtPercentile) {
        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + waitingtime.get(counter);

                counter++;
            }
            waitingtime.add((tempPercentile / (amountProcesses / 100)));
        }
    }

    static void run(List<Main.Process> processes,  List<Float> ntatPercentiles, List<Float> wtPercentiles){

        sortProcesses(processes);

        int amountProcesses = processes.size();
        // Display processes along with all details
        System.out.println("SRT");
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        // Calculate total waiting time and total turn
        // around time
        List<Integer> waitingtimes = new ArrayList<>();
        List<Float> normalizedTurnaroundtimes = new ArrayList<>();
        int starttime;
        int endtime;
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        Collections.sort(processes);
        for (Main.Process process: processes) {


            int waitingTime = (process.getClockCompleted() - process.getArrivaltime() - process.getServicetime());
            int turnAroundTime = (process.getClockCompleted() - process.getArrivaltime());
            float normalizedTurnAroundTime = ( (float) (process.getClockCompleted() - process.getArrivaltime()) / (float) process.getServicetime());

            // global parameters
            waitingtimes.add(waitingTime);
            normalizedTurnaroundtimes.add(normalizedTurnAroundTime);
            totalWaitingtime = (totalWaitingtime + waitingTime);
            totalTurnaroundtime = (totalTurnaroundtime + turnAroundTime);
            totalNormalizedTurnaroundtime = (totalNormalizedTurnaroundtime + normalizedTurnAroundTime);
            // start and end time
            starttime = process.getStartTime();
            endtime = process.getClockCompleted();

            // PROCESS PARAMS
            System.out.printf("     %-12s", process.getPid());
            System.out.printf(" %-13s", process.getArrivaltime());
            System.out.printf(" %-15s", process.getServicetime());
            System.out.printf(" %-15s", (process.getClockCompleted() - process.getServicetime() - process.getArrivaltime()));
            System.out.printf(" %-22s", process.getClockCompleted() - process.getArrivaltime());
            System.out.printf("%.2f                     ", ( (float) (process.getClockCompleted() - process.getArrivaltime()) / (float) process.getServicetime()));
            System.out.printf("%-10s", starttime);
            System.out.printf(" %-12s%n", endtime);
        }

        // calculating the averages from the totals / amount of processes
        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);
        float[] normalizedTurnaroundtimesfloatArray = new float[normalizedTurnaroundtimes.size()];
        int index = 0;
        for(Float value : normalizedTurnaroundtimes){
            normalizedTurnaroundtimesfloatArray[index] = value;
            index ++;
        }

        //ntatPercentile20k(amountProcesses, normalizedTurnaroundtimesfloatArray, ntatPercentiles);
        //wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);
        for(Main.Process process : processes){
            process.reset();
        }
    }

}

class RR {

    // method to find waitingtime for RR algorithm
    static void findWaitingtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes, int quantum) {
        // Make a copy of servicetimes to store remaining servicetimes
        int remainingServicetimes[] = new int[amountProcesses];
        for (int i = 0 ; i < amountProcesses ; i++)
            remainingServicetimes[i] =  processes.get(i).getServicetime();

        int t = 0; // Current time

        // Keep traversing processes in round robin manner
        // until all of them are not done.
        while(true)
        {
            boolean done = true;

            // Traverse all processes one by one repeatedly
            for (int i = 0 ; i < amountProcesses; i++)
            {
                // If burst time of a process is greater than 0
                // then only need to process further
                if (remainingServicetimes[i] > 0) {
                    done = false; // There is a pending process

                    if (remainingServicetimes[i] > quantum) {
                        // Increase the value of t i.e. shows
                        // how much time a process has been processed
                        t += quantum;

                        // Decrease the burst_time of current process
                        // by quantum
                        remainingServicetimes[i] -= quantum;
                    }
                    // If burst time is smaller than or equal to
                    // quantum. Last cycle for this process
                    else {
                        // Increase the value of t i.e. shows
                        // how much time a process has been processed
                        t = t + remainingServicetimes[i];

                        // Waiting time is current time minus time
                        // used by this process
                        waitingtimes[i] = t - processes.get(i).getServicetime();

                        // As the process gets fully executed
                        // make its remaining burst time = 0
                        remainingServicetimes[i] = 0;
                    }
                }
            }

            // If all processes are done
            if (done == true)
                break;
        }
    }

    // tat = bt + wt
    static void findTurnaroundtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes, int[] turnaroundtimes) {
        for (int i = 0; i < amountProcesses ; i++)
            turnaroundtimes[i] = processes.get(i).getServicetime() + waitingtimes[i];
    }

    static void findNormalizedTurnaroundtime(List<Main.Process> processes, int amountProcesses,  int[] turnaroundtimes, float[] normalizedTurnaroundtimes) {
        for (int i = 0; i < amountProcesses ; i++) {
            normalizedTurnaroundtimes[i] = (float) turnaroundtimes[i] / (float) processes.get(i).getServicetime();
        }
    }

    static void run(List<Main.Process> processes, int amountProcesses, List<Float> ntatPercentiles, List<Float> wtPercentiles, int q) {
        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        int starttime;
        int endtime = 0;

        // Function to find waiting time of all processes
        findWaitingtime(processes, amountProcesses, waitingtimes, q);
        // Function to find turn around time for all processes
        findTurnaroundtime(processes, amountProcesses, waitingtimes, turnaroundtimes);
        // Function to find normalized turn around time
        findNormalizedTurnaroundtime(processes, amountProcesses, turnaroundtimes, normalizedTurnaroundtimes);

        // Display processes along with all details
        System.out.println("04-06. Round Robin with slice:" + q);
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < amountProcesses; i++) {

            // global parameters
            totalWaitingtime = (totalWaitingtime + waitingtimes[i]);
            totalTurnaroundtime = totalTurnaroundtime + turnaroundtimes[i];
            totalNormalizedTurnaroundtime = totalNormalizedTurnaroundtime + ( (float) turnaroundtimes[i] / (float) processes.get(i).getServicetime());

            // start and end time
            starttime = 0;
            if(i != 0) {
                starttime = endtime + 1;
            }
            endtime = endtime + processes.get(i).getServicetime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", processes.get(i).getPid());
            System.out.printf(" %-13s", processes.get(i).getArrivaltime());
            System.out.printf(" %-15s", processes.get(i).getServicetime());
            System.out.printf(" %-15s", waitingtimes[i]);
            System.out.printf(" %-22s", turnaroundtimes[i]);
            System.out.printf("%.2f                     ", normalizedTurnaroundtimes[i]);
            System.out.printf("%-10s", starttime);
            System.out.printf(" %-12s%n", endtime);
        }

        // calculating the averages from the totals / amount of processes
        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);
        System.out.println("04-06. END slice:" + q + "          \n");

        //ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
        //wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);
    }
}

class HRRN {
    static void run(List<Main.Process> processes, int amountProcesses, List<Float> ntatPercentiles, List<Float> wtPercentiles) {

        // sort processes by arrivaltime
        Collections.sort(processes, Main.Process::compareToAT);

        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;

        int starttime;
        int endtime = 0;

        int time = 0;
        int sumST = 0;
        int i = 0;

        // sum of all service times
        for(int o = 0; o < amountProcesses; o++) {
            sumST += processes.get(o).getServicetime();
            processes.get(o).getServicetime();
        }

        System.out.println(sumST);

        System.out.println("07. highest response ratio next");
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        for (time = processes.get(0).getArrivaltime(); time < sumST;) {
            // Set lower limit to response ratio
            float hrr = -9999;

            // Response Ratio Variable
            float temp;

            // int to store the loc of the next process
            int loc = 0;
            for (i = 0; i < amountProcesses; i++) {

                // check if a process is there and or is complete
                if (processes.get(i).getArrivaltime() <= time && processes.get(i).getCompleted() != true) {

                    // calculate resposne ratio
                    //System.out.println("Process " + i+ ":    " + time + " " + processes.get(i).getArrivaltime() + " " + processes.get(i).getServicetime() + " " + processes.get(i).getServicetime());
                    temp = ( ((time - processes.get(i).getArrivaltime()) + processes.get(i).getServicetime()) / (float) processes.get(i).getServicetime());

                    // Checking for Highest Response Ratio
                    if (Float.compare(hrr, temp) < 0) {
                        //System.out.println("smaller");

                        // Storing Response Ratio
                        hrr = temp;

                        // Storing Location
                        loc = i;
                    }

                    //System.out.println(hrr);
                }
            }

            // Updating time value
            time += processes.get(loc).getServicetime();

            // calculating wt, tat, ntat
            waitingtimes[loc] = time - processes.get(loc).getArrivaltime() - processes.get(loc).getServicetime();
            turnaroundtimes[loc] = time - processes.get(loc).getArrivaltime();
            normalizedTurnaroundtimes[loc] = ((float) turnaroundtimes[loc] / processes.get(loc).getServicetime());

            // Updating Completion Status
            processes.get(loc).setCompleted();

            // keeping that totals
            totalWaitingtime += waitingtimes[loc];
            totalTurnaroundtime += turnaroundtimes[loc];
            totalNormalizedTurnaroundtime += normalizedTurnaroundtimes[loc];

            // PROCESS PARAMS
            System.out.printf("     %-12s", processes.get(loc).getPid());
            System.out.printf(" %-13s", processes.get(loc).getArrivaltime());
            System.out.printf(" %-15s", processes.get(loc).getServicetime());
            System.out.printf(" %-15s", waitingtimes[loc]);
            System.out.printf(" %-22s", turnaroundtimes[loc]);
            System.out.printf("%.2f                    \n", normalizedTurnaroundtimes[loc]);

        }

        // calculating the averages from the totals / amount of processes
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;
        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);

        System.out.println("07. END            \n");

        //ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
        //wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);*/
    }
}

class MLFB {

    // method to find waitingtime
    static void findWaitingtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes) {

    }

    // tat = bt + wt
    static void findTurnaroundtime(List<Main.Process> processes, int amountProcesses, int[] waitingtimes, int[] turnaroundtimes) {
    }

    static void findNormalizedTurnaroundtime(List<Main.Process> processes, int amountProcesses,  int[] turnaroundtimes, float[] normalizedTurnaroundtimes) {

    }

    static void run(List<Main.Process> processes, int amountProcesses, List<Float> ntatPercentiles, List<Float> wtPercentiles) {

    }
}

