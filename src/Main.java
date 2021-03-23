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
                        element.getAttribute("pid"),
                        element.getElementsByTagName("arrivaltime").item(0).getTextContent(),
                        element.getElementsByTagName("servicetime").item(0).getTextContent()
                );
                processes.add(process);
            }
        }

        // at this point we have a list of processes

        // print out all the processes
        for (Process process : processes) {
            System.out.println("pid: " + process.getPid());
            System.out.println("arrivaltime: " + process.getArrivaltime());
            System.out.println("servicetime: " + process.getServicetime());
        }
    }

    public static class Process {

        private String pid;
        private String arrivaltime;
        private String servicetime;

        public Process(String pid, String arrivaltime, String servicetime) {
            this.pid = pid;
            this.arrivaltime = arrivaltime;
            this.servicetime = servicetime;
        }

        public String getPid() {
            return pid;
        }

        public String getArrivaltime() {
            return arrivaltime;
        }

        public String getServicetime() {
            return servicetime;
        }
    }
}


