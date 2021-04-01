import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.List;

class LineChart_AWT extends ApplicationFrame {

    public static void main( String[ ] args ) {

    }

    public LineChart_AWT(String applicationTitle, String chartTitle, String charttype, List<Integer> stPercentiles, List<Float> percentilesFCFS, List<Float> percentilesSJF, List<Float> percentilesSRT, List<Float> percentilesRR2, List<Float> percentilesRR4, List<Float> percentilesRR8, List<Float> percentilesHRRN, List<Float> percentilesMLQF1, List<Float> percentilesMLQF2) {

        super(applicationTitle);

        JFreeChart lineChart;
        /* NTAT/ST chart for 10000 and 20000 processes -> change filename in Main.java  */
        if(charttype == "ntat") {
            lineChart = ChartFactory.createLineChart(
                    chartTitle,
                    "Service Time in Percentile","Normalized Turnaround Time",
                    createDatasetNTATST(stPercentiles, percentilesFCFS, percentilesSJF, percentilesSRT,  percentilesRR2,  percentilesRR4,  percentilesRR8,  percentilesHRRN,  percentilesMLQF1,  percentilesMLQF2),
                    PlotOrientation.VERTICAL,
                    true,true,false);
        } else {
           lineChart = ChartFactory.createLineChart(
                    chartTitle,
                    "Service Time in Percentile","Waiting Time",
                    createDatasetNTATST(stPercentiles, percentilesFCFS, percentilesSJF, percentilesSRT,  percentilesRR2,  percentilesRR4,  percentilesRR8,  percentilesHRRN,  percentilesMLQF1,  percentilesMLQF2),
                    PlotOrientation.VERTICAL,
                    true,true,false);
        }

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 650 ) );
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDatasetNTATST(List<Integer> stPercentiles, List<Float> ntatPercentilesFCFS, List<Float> ntatPercentilesSJF, List<Float> ntatPercentilesSRT, List<Float> ntatPercentilesRR2, List<Float> ntatPercentilesRR4, List<Float> ntatPercentilesRR8, List<Float> ntatPercentilesHRRN, List<Float> ntatPercentilesMLFQ1, List<Float> ntatPercentilesMLFQ2s) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        System.out.println(ntatPercentilesSRT);

        for(int i = 1; i < 100; i++) {
            dataset.addValue(ntatPercentilesFCFS.get(i), "FCFS" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesSJF.get(i), "SJF" , stPercentiles.get(i));
            //dataset.addValue(ntatPercentilesSRT.get(i), "SRT" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesRR2.get(i), "RR2" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesRR4.get(i), "RR4" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesRR8.get(i), "RR8" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesHRRN.get(i), "HRRN" , stPercentiles.get(i));
            //dataset.addValue(ntatPercentilesMLFQ1.get(i), "MLQF1" , stPercentiles.get(i));
            //dataset.addValue(ntatPercentilesMLFQ2.get(i), "MLQF2" , stPercentiles.get(i));*/
        }

        return dataset;
    }
}