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

// TBD
/* CURRENT LIST OF THINGS
// *************************
// find out what data we need and how to get/calculate it
// -----
// 01. PER PROCESS
// **   arrivaltime
// **   servicetime
// **   starttime
// **   end/finishtime
// **   TAT/MEAN TAT en wachtttijd
// -----
// 02. GLOBALE PARAMETERS BIJ TE HOUDEN
// ** gemiddelde omloopjd
// ** gemddelde genormaliseerde omlooptijd
// ** gemiddelde wachttijd

// *************************
// once we have the needed data we can start with the following algorithms
// 01. FCFS [DONE]
// 02. SJF [DONE]
// 03. SRT
// 04. RR q = 2 (timeslice)
// 04. RR q = 4
// 04. RR q = 88
// 05. HRRN
// 06. Multilevel feedback mode - 5 queues & timeslice zelf te bepalen en motiveer
// 07. Multilevel feedback mode - 5 queues & timeslice zelf te bepalen

// once we have ran our algorithms we have to find a way to compare/evaluate
// our data, pref in a graph or something
// 4 graphs for 10000 and 20000 processes
*/

class Main {
    public static void main(String[] args) throws Exception {

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
        List<Integer> pids = new ArrayList<Integer>();
        List<Integer> arrivaltimes = new ArrayList<Integer>();
        List<Integer> servicetimes = new ArrayList<Integer>();
        List<Integer> servicetimePercentiles = new ArrayList<Integer>();

        // 01. FCFS
        List<Float> ntatPercentilesFCFS = new ArrayList<>();
        List<Float> wtPercentilesFCFS = new ArrayList<>();

        // 02. SJF (send servicetimes sorted by high->low)
        List<Float> ntatPercentilesSJF = new ArrayList<>();
        List<Float> wtPercentilesSJF = new ArrayList<>();

        // print out all the processes
        for (Process process : processes) {
            System.out.println(process.getPid());
            pids.add(process.getPid());
            arrivaltimes.add(process.getArrivaltime());
            servicetimes.add(process.getServicetime());
        }

        // list of components of all processes, will be used in our algorithms
        // System.out.println(pids);
        // System.out.println(arrivaltimes);
        // System.out.println(servicetimes);

        // sort data on servicetimes

        // 01. FCFS
        FCFS.run(processes, amountProcesses, arrivaltimes, servicetimes, servicetimePercentiles, ntatPercentilesFCFS, wtPercentilesFCFS);

        // 02. SJF
        // SJF.run(processes, amountProcesses, arrivaltimes, servicetimes, servicetimePercentiles, ntatPercentilesSJF, wtPercentilesSJF);

        // 03.


        /*LineChart_AWT chart = new LineChart_AWT( "NTAT vs ST","Normalized Turnaround time vs Service Time", servicetimePercentiles, ntatPercentilesFCFS, ntatPercentilesSJF);

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );*/
    }

    public static class Process{

        private int pid;
        private int arrivaltime;
        private int servicetime;

        public Process(int pid, int arrivaltime, int servicetime) {
            this.pid = pid;
            this.arrivaltime = arrivaltime;
            this.servicetime = servicetime;
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
    }
}

// first come first serve
class FCFS {

    // Function to find the waiting time for all
    // processes
    static void findWaitingtime(int amountProcesses, List<Integer> servicetimes, List<Integer> arrivaltimes, int[] waitingtimes) {

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
            currentServicetime[i] = currentServicetime[i-1] + servicetimes.get(i-1);

            // Find waiting time for current process =
            // sum - at[i]
            waitingtimes[i] = currentServicetime[i] - arrivaltimes.get(i);

            // If waiting time for a process is in negative
            // that means it is already in the ready queue
            // before CPU becomes idle so its waiting time is 0
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
    static void findTurnaroundtime(int amountProcesses, List<Integer> servicetimes, int[] waitingtimes, int[] turnaroundtimes) {

        // Calculating turnaround time by adding servicetimes and waitingtimes
        for (int i = 0; i < amountProcesses ; i++) {
            turnaroundtimes[i] = servicetimes.get(i) + waitingtimes[i];
        }
    }

    // Function to calculate normalized
    static void findNormalizedTurnaroundtime(int amountProcesses, List<Integer> servicetimes,  int[] turnaroundtimes, float[] normalizedTurnaroundtimes) {

        // Calculating normalized turnaround time by diving turnaroudtimes with servicetimes
        for (int i = 0; i < amountProcesses ; i++) {
            normalizedTurnaroundtimes[i] = (float) turnaroundtimes[i] / (float) servicetimes.get(i);
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

    //Function to calculate average time
    static void run(List<Main.Process> processes, int amountProcesses, List<Integer> arrivaltimes, List<Integer> servicetimes, List<Integer> servicetimesPercentiles, List<Float> ntatPercentiles, List<Float> wtPercentiles) {
        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        int starttime;
        int endtime = 0;

        HashMap<Integer, List<Float>> hm = new HashMap<Integer, List<Float>>();

        // Function to find waiting time of all processes
        findWaitingtime(amountProcesses, servicetimes, arrivaltimes, waitingtimes);

        // Function to find turn around time for all processes
        findTurnaroundtime(amountProcesses, servicetimes, waitingtimes, turnaroundtimes);

        // Function to find normalized turn around time
        findNormalizedTurnaroundtime(amountProcesses, servicetimes, turnaroundtimes, normalizedTurnaroundtimes);

        // Display processes along with all details
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < amountProcesses; i++) {

            // global parameters
            totalWaitingtime = ( totalWaitingtime + waitingtimes[i]);
            totalTurnaroundtime = totalTurnaroundtime + turnaroundtimes[i];
            totalNormalizedTurnaroundtime = totalNormalizedTurnaroundtime + ( (float) turnaroundtimes[i] / (float) servicetimes.get(i));

            starttime = 0;

            if(i != 0) {
                starttime = endtime + 1;
            }

            endtime = endtime + servicetimes.get(i);

            // PROCESS PARAMS
            System.out.printf("     %-12s", (i + 1));
            System.out.printf(" %-13s", arrivaltimes.get(i));
            System.out.printf(" %-15s", servicetimes.get(i));
            System.out.printf(" %-15s", waitingtimes[i]);
            System.out.printf(" %-22s", turnaroundtimes[i]);
            System.out.printf("%.2f                     ", normalizedTurnaroundtimes[i]);
            System.out.printf("%-10s", starttime);
            System.out.printf(" %-12s%n", endtime);
        }

        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);

        // sum and them devide by 100
        //stPercentile20k(amountProcesses, servicetimes, servicetimesPercentiles);

        // sum and them devide by 100
        //ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
    }
}

class SJF {
    // Function to find the waiting time for all
    // processes
    static void findWaitingtime(int amountProcesses, List<Integer> servicetimes, List<Integer> arrivaltimes, int[] waitingtimes) {

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
            currentServicetime[i] = currentServicetime[i-1] + servicetimes.get(i-1);

            // Find waiting time for current process =
            // sum - at[i]
            waitingtimes[i] = currentServicetime[i] - arrivaltimes.get(i);

            // If waiting time for a process is in negative
            // that means it is already in the ready queue
            // before CPU becomes idle so its waiting time is 0
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
    static void findTurnaroundtime(int amountProcesses, List<Integer> servicetimes, int[] waitingtimes, int[] turnaroundtimes) {

        // Calculating turnaround time by adding servicetimes and waitingtimes
        for (int i = 0; i < amountProcesses ; i++) {
            turnaroundtimes[i] = servicetimes.get(i) + waitingtimes[i];
        }
    }

    // Function to calculate normalized
    static void findNormalizedTurnaroundtime(int amountProcesses, List<Integer> servicetimes,  int[] turnaroundtimes, float[] normalizedTurnaroundtimes) {

        // Calculating normalized turnaround time by diving turnaroudtimes with servicetimes
        for (int i = 0; i < amountProcesses ; i++) {
            normalizedTurnaroundtimes[i] = (float) turnaroundtimes[i] / (float) servicetimes.get(i);
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

    //Function to calculate average time
    static void run(List<Main.Process> processes, int amountProcesses, List<Integer> arrivaltimes, List<Integer> servicetimes, List<Integer> servicetimesPercentiles, List<Float> ntatPercentiles, List<Float> wtPercentiles) {

        Collections.sort(servicetimes);

        int waitingtimes[] = new int[amountProcesses];
        int[] turnaroundtimes = new int[amountProcesses];
        float[] normalizedTurnaroundtimes = new float[amountProcesses];
        int totalWaitingtime = 0;
        int totalTurnaroundtime = 0;
        float totalNormalizedTurnaroundtime = 0;
        int starttime;
        int endtime = 0;

        HashMap<Integer, List<Float>> hm = new HashMap<Integer, List<Float>>();

        // Function to find waiting time of all processes
        findWaitingtime(amountProcesses, servicetimes, arrivaltimes, waitingtimes);

        // Function to find turn around time for all processes
        findTurnaroundtime(amountProcesses, servicetimes, waitingtimes, turnaroundtimes);

        // Function to find normalized turn around time
        findNormalizedTurnaroundtime(amountProcesses, servicetimes, turnaroundtimes, normalizedTurnaroundtimes);

        // Display processes along with all details
        System.out.printf("processes -- arrivaltime -- servicetime --  waitingtime -- turnaroundtime -- normalized tunraroundtime --  starttime -- endtime \n");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < amountProcesses; i++) {

            // global parameters
            totalWaitingtime = ( totalWaitingtime + waitingtimes[i]);
            totalTurnaroundtime = totalTurnaroundtime + turnaroundtimes[i];
            totalNormalizedTurnaroundtime = totalNormalizedTurnaroundtime + ( (float) turnaroundtimes[i] / (float) servicetimes.get(i));

            starttime = 0;

            if(i != 0) {
                starttime = endtime + 1;
            }

            endtime = endtime + servicetimes.get(i);

            // PROCESS PARAMS
            System.out.printf("     %-12s", (i + 1));
            System.out.printf(" %-13s", arrivaltimes.get(i));
            System.out.printf(" %-15s", servicetimes.get(i));
            System.out.printf(" %-15s", waitingtimes[i]);
            System.out.printf(" %-22s", turnaroundtimes[i]);
            System.out.printf("%.2f                     ", normalizedTurnaroundtimes[i]);
            System.out.printf("%-10s", starttime);
            System.out.printf(" %-12s%n", endtime);
        }

        float averageTurnaroundtime = (float) totalTurnaroundtime / (float) amountProcesses;
        float averageNormalizedTurnaroundtime =  totalNormalizedTurnaroundtime /  (float) amountProcesses;
        float averageWaitingtime =   (float) totalWaitingtime /  (float) amountProcesses;

        // GLOBAL PARAMS
        System.out.printf("Average TAT: %.2f %n", averageTurnaroundtime);
        System.out.printf("Average Normalized TAT: %.2f %n",averageNormalizedTurnaroundtime);
        System.out.printf("Average Waitingtime: %.2f %n", averageWaitingtime);

        // sum and them devide by 100
        stPercentile20k(amountProcesses, servicetimes, servicetimesPercentiles);

        // sum and them devide by 100
        ntatPercentile20k(amountProcesses, normalizedTurnaroundtimes, ntatPercentiles);
    }
}

