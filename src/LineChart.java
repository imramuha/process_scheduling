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

    public LineChart_AWT(String applicationTitle, String chartTitle, List<Integer> stPercentiles, List<Float> ntatPercentilesFCFS, List<Float> ntatPercentilesSJF) {

        super(applicationTitle);

        /* ntat/st chart
        *
        *
        */
        JFreeChart lineChartntat = ChartFactory.createLineChart(
                chartTitle,
                "Service Time in Percentile","Normalized Turnaround Time",
                createDataset(stPercentiles, ntatPercentilesFCFS, ntatPercentilesSJF),
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
                createDataset(stPercentiles, ntatPercentilesFCFS, ntatPercentilesSJF),
                PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanelwt = new ChartPanel( lineChartwt );
        chartPanelwt.setPreferredSize( new java.awt.Dimension( 1000 , 650 ) );
        setContentPane(chartPanelwt);*/
    }

    private DefaultCategoryDataset createDataset(List<Integer> stPercentiles, List<Float> ntatPercentilesFCFS, List<Float> ntatPercentilesSJF) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        for(int i = 0; i < 100; i++) {
            dataset.addValue(ntatPercentilesFCFS.get(i), "FCFS" , stPercentiles.get(i));
        }

        /*for(int i = 0; i < 100; i++) {
            dataset.addValue(ntatPercentilesSJF.get(i), "SJF" , stPercentiles.get(i));
        }*/

        return dataset;
    }
}