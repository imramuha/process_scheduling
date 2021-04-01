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
        FCFS.run(processes, amountProcesses, ntatPercentilesFCFS, wtPercentilesFCFS);

        // 02. SJF (send servicetimes sorted by high->low)
        List<Float> ntatPercentilesSJF = new ArrayList<>();
        List<Float> wtPercentilesSJF = new ArrayList<>();
        SJF.run(processes, amountProcesses, ntatPercentilesSJF, wtPercentilesSJF);

        // 03. SRT
        List<Float> ntatPercentilesSRT = new ArrayList<>();
        List<Float> wtPercentilesSRT = new ArrayList<>();
        //SRT.run(processes, ntatPercentilesSRT, wtPercentilesSRT);

        // 04-06. RR with different q's = 2, 4 en 8.
        int q2 = 2;
        int q4 = 4;
        int q8 = 8;
        List<Float> ntatPercentilesRR2 = new ArrayList<>();
        List<Float> wtPercentilesRR2 = new ArrayList<>();
        List<Float> ntatPercentilesRR4 = new ArrayList<>();
        List<Float> wtPercentilesRR4 = new ArrayList<>();
        List<Float> ntatPercentilesRR8 = new ArrayList<>();
        List<Float> wtPercentilesRR8 = new ArrayList<>();
        RR.run(processes, q2, ntatPercentilesRR2, wtPercentilesRR2);
        RR.run(processes, q4, ntatPercentilesRR4, wtPercentilesRR4);
        RR.run(processes, q8, ntatPercentilesRR8, wtPercentilesRR8);

        // 07. HRRN
        List<Float> ntatPercentilesHRRN = new ArrayList<>();
        List<Float> wtPercentilesHRRN = new ArrayList<>();
        HRRN.run(processes, amountProcesses, ntatPercentilesHRRN, wtPercentilesHRRN);

        // 08-09. Multilevel Feedback met 2 verschillende queues [TBD]
        List<Float> ntatPercentilesMLFB1 = new ArrayList<>();
        List<Float> wtPercentilesMLFB1 = new ArrayList<>();
        List<Float> ntatPercentilesMLFB2 = new ArrayList<>();
        List<Float> wtPercentilesMLFB2 = new ArrayList<>();
        //MLFB.run(processes, amountProcesses, ntatPercentilesMLFB, wtPercentilesMLFB);

        // get our ST percentiles for the x-axis
        List<Integer> stPercentiles = new ArrayList<Integer>();
        //stPercentile20k(processes, amountProcesses, stPercentiles);

        //
        // Charts
        //
        // This is where we consume our data to create line charts through LineChart.java (jFreeChart library)
        // Since we have two different type of charts, uncomment whichever you desire to see
        //String charttype = "ntat";
        /*String charttype = "wt";

        LineChart_AWT chart;

        // NTAT vs ST chart
        if(charttype == "ntat") {
            chart = new LineChart_AWT( "NTAT vs ST","Normalized Turnaround time vs Service Time", "ntat",  stPercentiles, ntatPercentilesFCFS, ntatPercentilesSJF, ntatPercentilesSRT, ntatPercentilesRR2, ntatPercentilesRR4, ntatPercentilesRR8, ntatPercentilesHRRN, ntatPercentilesMLFB1, ntatPercentilesMLFB2);
        } else {
            chart = new LineChart_AWT( "WT vs ST","Waiting times vs Service Time", "wt", stPercentiles, wtPercentilesFCFS, wtPercentilesSJF, wtPercentilesSRT, wtPercentilesRR2, wtPercentilesRR4, wtPercentilesRR8, wtPercentilesHRRN, wtPercentilesMLFB1, wtPercentilesMLFB2);
        }

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible(true);*/
    }

    public static class Process implements Comparable<Process> {

        private int pid;
        private int arrivaltime;
        private int servicetime;
        private int completionTime;
        private boolean completed;
        private int serviceTimeLeft;
        private int clockCompleted;
        private int startTime;

        public Process(int pid, int arrivaltime, int servicetime) {
            this.pid = pid;
            this.arrivaltime = arrivaltime;
            this.servicetime = servicetime;
            this.completionTime = 0;
            this.completed = false;
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

        public int getCompletionTime() {
            return completionTime;
        }

        public void setCompletionTime(int completionTime){
            this.completionTime = completionTime;
        }

        public void oneClock(){
            if(serviceTimeLeft == 0){
                //Should never happen
                System.out.println("OneClock command thrown with no service time left for process" + this.pid + " with service time " + this.servicetime);
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

        public void reset(){
            this.completed = false;
            this.serviceTimeLeft = this.servicetime;
            this.clockCompleted = 0;
            this.completionTime = 0;
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

        System.out.println("01. END - first come first serve            \n");


        for(Main.Process process : processes){
            process.reset();
        }
    }
}

class SJF {

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

        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        int starttime;
        int endtime = 0;
        int totalCompletedProcesses = 0;
        int systemtime = 0;

        // Display processes along with all details
        System.out.println("02. shortest job first");
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        while(true) {
            int x = 0;
            int min = 999;

            // if amountProcesses == totalamount of completed processes, loop it'll be terminated
            if (totalCompletedProcesses == amountProcesses)
                break;
            for (int i = 0; i < amountProcesses; i++) {

                if ((processes.get(i).getArrivaltime() <= systemtime) && !(processes.get(i).getCompleted()) && (processes.get(i).getServicetime() < min))  {
                    min = processes.get(i).getServicetime();
                    x = i;
                }

            }

            // If x == amountprocesses, means x can not be updated because no AT is < than ST (systemtime)
            if (x == amountProcesses) {
                systemtime++;
            } else {
                // params per process
                processes.get(x).setCompletionTime(systemtime + processes.get(x).getServicetime());
                systemtime += processes.get(x).getServicetime();
                turnaroundtimes[x] = processes.get(x).getCompletionTime() - processes.get(x).getArrivaltime();
                waitingtimes[x] = turnaroundtimes[x] - processes.get(x).getServicetime();
                normalizedTurnaroundtimes[x] =  turnaroundtimes[x] / processes.get(x).getServicetime();
                processes.get(x).setCompleted();
                totalCompletedProcesses++;
            }


            // global parameters
            totalWaitingtime = (totalWaitingtime + waitingtimes[x]);
            totalTurnaroundtime = totalTurnaroundtime + turnaroundtimes[x];
            totalNormalizedTurnaroundtime = totalNormalizedTurnaroundtime + ( (float) turnaroundtimes[x] / (float) processes.get(x).getServicetime());

            // start and end time
            starttime = 0;
            if(x != 0) {
                starttime = endtime + 1;
            }
            endtime = endtime + processes.get(x).getServicetime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", processes.get(x).getPid());
            System.out.printf(" %-13s", processes.get(x).getArrivaltime());
            System.out.printf(" %-15s", processes.get(x).getServicetime());
            System.out.printf(" %-15s", waitingtimes[x]);
            System.out.printf(" %-22s", turnaroundtimes[x]);
            System.out.printf("%.2f                     ", normalizedTurnaroundtimes[x]);
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

        System.out.println("02. END - shortest job first           \n");

        ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
        wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);

        for(Main.Process process : processes){
            process.reset();
        }
    }
}

//Shortest remaining time
class SRT {

    // Function to find the waiting time for all
    static void sortProcesses(List<Main.Process> processes) {

        processes.sort(Main.Process::compareToAT);
        List<Main.Process> waitingProcessList = new ArrayList<>();
        Main.Process currentProcess = null;
        int clock = 0;


        // calculating waiting time and save in a list
        for (Main.Process process : processes) {

            while (clock != process.getArrivaltime()) {

                if (currentProcess != null) {

                    if (currentProcess.getCompleted()) {
                        currentProcess.setCompletionTime(clock);
                        if (!waitingProcessList.isEmpty()) {
                            waitingProcessList.sort(Main.Process::compareToSTLeft);
                            currentProcess = waitingProcessList.get(0);
                            waitingProcessList.remove(0);
                            if (currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()) {
                                currentProcess.setStartTime(clock);
                            }
                            clock += 1;
                            currentProcess.oneClock();
                        }
                        else{
                            currentProcess = null;
                        }
                    }
                    else{
                        clock += 1;
                        currentProcess.oneClock();
                    }
                } else {
                    clock += 1;
                }
            }
            if (currentProcess == null || currentProcess.getServiceTimeLeft() > process.getServiceTimeLeft()) {
                if (currentProcess != null) {
                    if(currentProcess.getCompleted()){
                        currentProcess.setCompletionTime(clock);
                    }
                    else{
                        waitingProcessList.add(currentProcess);
                    }
                    if (currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()) {
                        currentProcess.setStartTime(clock);
                    }
                }
                currentProcess = process;
                if (currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()) {
                    currentProcess.setStartTime(clock);
                }
                currentProcess.oneClock();
                clock += 1;

            } else {
                waitingProcessList.add(process);
                if (currentProcess.getCompleted()) {
                    currentProcess.setCompletionTime(clock);
                    if (!waitingProcessList.isEmpty()) {
                        Collections.sort(waitingProcessList, Main.Process::compareToSTLeft);
                        currentProcess = waitingProcessList.get(0);
                        if (currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()) {
                            currentProcess.setStartTime(clock);
                        }
                    }
                    else{
                        currentProcess = null;
                    }
                }
                if(currentProcess != null){
                    currentProcess.oneClock();
                }
                clock += 1;
            }
        }
        Collections.sort(waitingProcessList, Main.Process::compareToSTLeft);
        for(Main.Process process : waitingProcessList){
            while(!process.getCompleted()){
                if (process.getServicetime() == process.getServiceTimeLeft()) {
                    process.setStartTime(clock);
                }
                clock += 1;
                process.oneClock();
                if(process.getCompleted()){
                    process.setCompletionTime(clock);
                }
            }
        }
    }

    // Function to calculate percentile
    static void ntatPercentile20k(int amountProcesses, List<Float> normalizedTurnaroundtimes, List<Float> ntatPercentile) {

        int counter = 0;

        // Calculating percentile for 20k
        for (int i = 0; i < 100 ; i++) {
            float tempPercentile = 0;

            for(int j = 0; j < (amountProcesses / 100); j++) {

                tempPercentile= tempPercentile + normalizedTurnaroundtimes.get(counter);

                counter++;
            }
            ntatPercentile.add(tempPercentile / (amountProcesses / 100));
        }
    }

    // Function to calculate percentile
    static void wtPercentile20k(int amountProcesses, List<Integer> waitingtimes, List<Float> wtPercentile) {
        int counter = 0;

        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;
            for(int j = 0; j < (amountProcesses / 100); j++) {
                tempPercentile= tempPercentile + waitingtimes.get(counter);
                counter++;
            }
            wtPercentile.add((float) tempPercentile / (amountProcesses / 100));
        }

    }

    static void run(List<Main.Process> processes,  List<Float> ntatPercentiles, List<Float> wtPercentiles){

        processes.sort(Main.Process::compareToAT);
        int amountProcesses = processes.size();
        // Display processes along with all details
        System.out.println("SRT");
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");
        sortProcesses(processes);
        // Calculate total waiting time and total turn
        // around time
        List<Integer> waitingtimes = new ArrayList<>();
        List<Float> normalizedTurnaroundtimes = new ArrayList<>();
        int starttime;
        int endtime;
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        for (Main.Process process: processes) {
            int waitingTime = (process.getCompletionTime() - process.getArrivaltime() - process.getServicetime());
            int turnAroundTime = (process.getCompletionTime() - process.getArrivaltime());
            float normalizedTurnAroundTime = ( (float) turnAroundTime / (float) process.getServicetime());

            // global parameters
            waitingtimes.add(waitingTime);
            normalizedTurnaroundtimes.add(normalizedTurnAroundTime);
            totalWaitingtime = (totalWaitingtime + waitingTime);
            totalTurnaroundtime = (totalTurnaroundtime + turnAroundTime);
            totalNormalizedTurnaroundtime = (totalNormalizedTurnaroundtime + normalizedTurnAroundTime);
            // start and end time
            starttime = process.getStartTime();
            endtime = process.getCompletionTime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", process.getPid());
            System.out.printf(" %-13s", process.getArrivaltime());
            System.out.printf(" %-15s", process.getServicetime());
            System.out.printf(" %-15s", waitingTime);
            System.out.printf(" %-22s", turnAroundTime);
            System.out.printf("%.2f                     ", normalizedTurnAroundTime);
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

        for(Main.Process process : processes){
            process.reset();
        }
    }

}

class RR {

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
    static void wtPercentile20k(int amountProcesses, List<Integer> waitingtimes, List<Float> wtPercentile) {
        int counter = 0;

        for (int i = 0; i < 100 ; i++) {
            int tempPercentile = 0;
            for(int j = 0; j < (amountProcesses / 100); j++) {
                tempPercentile= tempPercentile + waitingtimes.get(counter);
                counter++;
            }
            wtPercentile.add((float) tempPercentile / (amountProcesses / 100));
        }
    }

    static int sliceProcess(Main.Process process, int slice){
        int countslice = 0;
        while(!process.getCompleted() && countslice < slice){
                process.oneClock();
                countslice ++;
            }
        return countslice;
    }

    // method to find waitingtime for RR algorithm
    static void sortProcesses(List<Main.Process> processes, int slice) {
        processes.sort(Main.Process::compareToAT);
        int t = 0; // Current time
        // Keep traversing processes in round robin manner
        // until all of them are not done.
        // calculating waiting time and save in a list
        LinkedList<Main.Process> waitingProcessList = new LinkedList<>();
        Main.Process currentProcess;
        for (Main.Process process : processes) {
            while (t < process.getArrivaltime()) {
                if(waitingProcessList.isEmpty()){
                    t +=1;
                }
                for(int i = 0; i < waitingProcessList.size(); i++){
                    currentProcess = waitingProcessList.removeFirst();
                    if(currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()){
                        currentProcess.setStartTime(t);
                    }
                    //System.out.println(currentProcess.getPid());
                    t += sliceProcess(currentProcess, slice);
                    //System.out.println("counter: " + t);
                    if(t >= process.getArrivaltime()){
                        if(!waitingProcessList.contains(process) && currentProcess != process){
                            //System.out.println("AT: " + process.getArrivaltime() + " T: " + t);
                            //System.out.println("added 1" + process.getPid());
                            waitingProcessList.add(process);
                        }
                    }
                    if(currentProcess.getCompleted()){
                        currentProcess.setCompletionTime(t);
                    }
                    else{
                        waitingProcessList.add(currentProcess);
                    }
                    }
                }
            if(!waitingProcessList.contains(process)){
                waitingProcessList.add(process);
            }
        }
            /*
            StringBuilder sb = new StringBuilder();
            for(Main.Process i : waitingProcessList){
                sb.append(i.getPid() + " ");
            }
            System.out.println(sb.toString());*/
        while(!waitingProcessList.isEmpty()){
            currentProcess = waitingProcessList.removeFirst();
            if(currentProcess.getServicetime() == currentProcess.getServiceTimeLeft()){
                        currentProcess.setStartTime(t);
            }
            System.out.println(currentProcess.getPid());
            t += sliceProcess(currentProcess, slice);
            if(currentProcess.getCompleted()){
                currentProcess.setCompletionTime(t);
            }
            else{
                waitingProcessList.add(currentProcess);
            }
        }
    }

    static void run(List<Main.Process> processes, int slice, List<Float> ntatPercentiles, List<Float> wtPercentiles) {

        sortProcesses(processes, slice);

        int amountProcesses = processes.size();
        // Display processes along with all details
        System.out.println("RR slice " + slice);
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


            int waitingTime = (process.getCompletionTime() - process.getArrivaltime() - process.getServicetime());
            int turnAroundTime = (process.getCompletionTime() - process.getArrivaltime());
            float normalizedTurnAroundTime = ( (float) (process.getCompletionTime() - process.getArrivaltime()) / (float) process.getServicetime());

            // global parameters
            waitingtimes.add(waitingTime);
            normalizedTurnaroundtimes.add(normalizedTurnAroundTime);


            totalWaitingtime = (totalWaitingtime + waitingTime);
            totalTurnaroundtime = (totalTurnaroundtime + turnAroundTime);
            totalNormalizedTurnaroundtime = (totalNormalizedTurnaroundtime + normalizedTurnAroundTime);
            // start and end time
            starttime = process.getStartTime();
            endtime = process.getCompletionTime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", process.getPid());
            System.out.printf(" %-13s", process.getArrivaltime());
            System.out.printf(" %-15s", process.getServicetime());
            System.out.printf(" %-15s", (process.getCompletionTime() - process.getServicetime() - process.getArrivaltime()));
            System.out.printf(" %-22s", process.getCompletionTime() - process.getArrivaltime());
            System.out.printf("%.2f                     ", ( (float) (process.getCompletionTime() - process.getArrivaltime()) / (float) process.getServicetime()));
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

        ntatPercentile20k(amountProcesses, normalizedTurnaroundtimesfloatArray, ntatPercentiles);
        wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);


        for(Main.Process process : processes){
            process.reset();
        }
    }
}

class HRRN {
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
                    temp = ( ((time - processes.get(i).getArrivaltime()) + processes.get(i).getServicetime()) / (float) processes.get(i).getServicetime());

                    // Checking for Highest Response Ratio
                    if (Float.compare(hrr, temp) < 0) {
                        //System.out.println("smaller");

                        // Storing Response Ratio
                        hrr = temp;

                        // Storing Location
                        loc = i;
                    }
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

            // start and end time
            starttime = 0;
            if(i != 0) {
                starttime = endtime + 1;
            }
            endtime = endtime + processes.get(loc).getServicetime();

            // PROCESS PARAMS
            System.out.printf("     %-12s", processes.get(loc).getPid());
            System.out.printf(" %-13s", processes.get(loc).getArrivaltime());
            System.out.printf(" %-15s", processes.get(loc).getServicetime());
            System.out.printf(" %-15s", waitingtimes[loc]);
            System.out.printf(" %-22s", turnaroundtimes[loc]);
            System.out.printf(" %.2f                   ", normalizedTurnaroundtimes[loc]);
            System.out.printf(" %-10s", starttime);
            System.out.printf("%-25s                   \n", endtime);

        }

        // calculating the averages from the totals / amount of processes
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;
        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);

        System.out.println("07. highest response ratio next - END            \n");

        ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
        wtPercentile20k(amountProcesses, waitingtimes, wtPercentiles);

        for(Main.Process process : processes){
            process.reset();
        }
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

