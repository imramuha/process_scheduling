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

    public LineChart_AWT(String applicationTitle, String chartTitle, List<Integer> stPercentiles, List<Float> ntatPercentilesFCFS, List<Float> ntatPercentilesSJF, List<Float> ntatPercentilesSRT, List<Float> ntatPercentilesRR2, List<Float> ntatPercentilesRR4, List<Float> ntatPercentilesRR8, List<Float> ntatPercentilesHRRN, List<Float> ntatPercentilesMLQF1, List<Float> ntatPercentilesMLQF2) {

        super(applicationTitle);

        /* ntat/st chart
        *
        *
        */
        JFreeChart lineChartntat = ChartFactory.createLineChart(
                chartTitle,
                "Service Time in Percentile","Normalized Turnaround Time",
                createDatasetNTATST(stPercentiles, ntatPercentilesFCFS, ntatPercentilesSJF, ntatPercentilesSRT,  ntatPercentilesRR2,  ntatPercentilesRR4,  ntatPercentilesRR8,  ntatPercentilesHRRN,  ntatPercentilesMLQF1,  ntatPercentilesMLQF2),
                PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanelntat = new ChartPanel( lineChartntat );
        chartPanelntat.setPreferredSize( new java.awt.Dimension( 1000 , 650 ) );
        setContentPane(chartPanelntat);

        /*
        *
        *
        */
        // Uncomment this and comment the previous /* */  to see the wt/st chart
        /*JFreeChart lineChartwt = ChartFactory.createLineChart(
                chartTitle,
                "Service Time in Percentile","Normalized Turnaround Time",
                createDatasetWTST(stPercentiles, ntatPercentilesFCFS, ntatPercentilesSJF, wtPercentilesSJF, wtPercentilesSRT,  wtPercentilesRR2,  wtPercentilesRR4,  wtPercentilesRR8,  wtPercentilesHRRN,  wtPercentilesMLQF1,  wtPercentilesMLQF2)
                PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanelwt = new ChartPanel( lineChartwt );
        chartPanelwt.setPreferredSize( new java.awt.Dimension( 1000 , 650 ) );
        setContentPane(chartPanelwt);*/
    }

    private DefaultCategoryDataset createDatasetNTATST(List<Integer> stPercentiles, List<Float> ntatPercentilesFCFS, List<Float> ntatPercentilesSJF, List<Float> ntatPercentilesSRT, List<Float> ntatPercentilesRR2, List<Float> ntatPercentilesRR4, List<Float> ntatPercentilesRR8, List<Float> ntatPercentilesHRRN, List<Float> ntatPercentilesMLFQ1, List<Float> ntatPercentilesMLFQ2s) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        // we need to set max values
        for(int i = 1; i < 100; i++) {
            dataset.addValue(ntatPercentilesFCFS.get(i), "FCFS" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesSJF.get(i), "SJF" , stPercentiles.get(i));
           /* dataset.addValue(ntatPercentilesSRT.get(i), "SRT" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesRR2.get(i), "RR2" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesRR4.get(i), "RR4" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesRR8.get(i), "RR8" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesHRRN.get(i), "HRRN" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesMLQF1.get(i), "MLQF1" , stPercentiles.get(i));
            dataset.addValue(ntatPercentilesMLQF2.get(i), "MLQF2" , stPercentiles.get(i));*/

        }

        return dataset;
    }

    // WT vs ST
    /*private DefaultCategoryDataset createDatasetWTST(List<Integer> stPercentiles, List<Float> wtPercentilesFCFS, List<Float> wtPercentilesSJF, List<Float> wtPercentilesSRT, List<Float> wtPercentilesRR2,List<Float> wtPercentilesRR4, List<Float> wtPercentilesRR8, List<Float> wtPercentilesHRRN, List<Float> wtPercentilesMLFQ1, List<Float> wtPercentilesMLFQ2) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        // we need to set max values
        for(int i = 0; i < 100; i++) {
            dataset.addValue(wtPercentilesFCFS.get(i), "FCFS" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesSJF.get(i), "SJF" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesSRT.get(i), "SRT" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesRR2.get(i), "RR2" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesRR4.get(i), "RR4" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesRR8.get(i), "RR8" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesHRRN.get(i), "HRRN" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesMLFQ1.get(i), "MLFQ1" , stPercentiles.get(i));
            dataset.addValue(wtPercentilesMLFQ2.get(i), "MLFQ2" , stPercentiles.get(i));
        }

        return dataset;
    }*/
}