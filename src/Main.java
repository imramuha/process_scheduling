import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
// 01. FCFS
// 02. SJF
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

        File xmlFile = new File("processen20000.xml");
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
        List<Integer> pids = new ArrayList<>();
        List<Integer> arrivaltimes = new ArrayList<>();
        List<Integer> servicetimes = new ArrayList<>();

        int amountProcesses = processes.size();
        System.out.println(processes.size());

        // print out all the processes
        for (Process process : processes) {
           pids.add(process.getPid());
           arrivaltimes.add(process.getArrivaltime());
           servicetimes.add(process.getServicetime());
        }

        // list of components of all processes, will be used in our algorithms
        System.out.println(pids);
        System.out.println(arrivaltimes);
        System.out.println(servicetimes);

        // 01. FCFS
        FCFS.findAverageTAT(processes, amountProcesses, arrivaltimes, servicetimes );
        // FCFS.gemiddeldeTAT :)
        // FCFS.gemiddeldeWachttijd :)
    }

    public static class Process {

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
    static void findWaitingTime(List<Main.Process> processes, int amountProcesses,
                                List<Integer> servictimes, int[] waitingtimes) {

        // waiting time for first process is 0
        waitingtimes[0] = 0;

        // calculating waiting time
        for (int i = 1; i < amountProcesses; i++) {
            waitingtimes[i] = servictimes.get(i-1) + waitingtimes[i - 1];
        }
    }

    // Function to calculate turn around time
    static void findTurnAroundTime(List<Main.Process> processes, int amountProcesses,
                                   List<Integer> servictimes, int[] waitingtimes, int[] TATs) {

        // calculating turnaround time by adding
        // bt[i] + wt[i]
        for (int i = 0; i < amountProcesses; i++) {
            TATs[i] = servictimes.get(i) + waitingtimes[i];
        }
    }

    //Function to calculate average time
    static void findAverageTAT(List<Main.Process> processes, int amountProcesses, List<Integer> arrivaltimes, List<Integer> servicetimes) {
        int waitingtimes[] = new int[amountProcesses];
        int TATs[] = new int[amountProcesses];
        int totalWaitingtime = 0;
        int totalTAT = 0;

        //Function to find waiting time of all processes
        findWaitingTime(processes, amountProcesses, arrivaltimes, waitingtimes);

        //Function to find turn around time for all processes
        findTurnAroundTime(processes, amountProcesses, arrivaltimes, waitingtimes, TATs);

        //Display processes along with all details
        System.out.printf("Processes Burst time Waiting"
                +" time Turn around time\n");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < amountProcesses; i++) {
            totalWaitingtime = totalWaitingtime + waitingtimes[i];
            totalTAT = totalTAT + TATs[i];
            System.out.printf("%d ", (i + 1));
            System.out.printf("%d ", servicetimes.get(i));
            System.out.printf("%d", waitingtimes[i]);
            System.out.printf("%d\n", TATs[i]);
        }

        float s = (float)totalWaitingtime /(float) amountProcesses;
        int t = totalWaitingtime / amountProcesses;
        System.out.printf("Average waiting time = %f", s);
        System.out.printf("\n");
        System.out.printf("Average turn around time = %d ", t);
    }
}


