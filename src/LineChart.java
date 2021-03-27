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

    public LineChart_AWT( String applicationTitle , String chartTitle, List<Integer> servicetimes ) {

        super(applicationTitle);

        // hier de processen verdelen in 10 / 100 processen en doorgeven naar create Dataset

        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Service Time in Percentile","Normalized Turnaround Time",
                createDataset(),
                PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );


        dataset.addValue( 0 , "schools" , "10" );
        dataset.addValue( 10 , "schools" , "20" );
        dataset.addValue( 10 , "schools" , "30" );
        dataset.addValue( 10 , "schools" , "40" );
        dataset.addValue( 10 , "schools" , "50" );
        dataset.addValue( 10 , "schools" , "60" );
        dataset.addValue( 10 , "schools" , "70" );
        dataset.addValue( 10, "schools" , "80" );
        dataset.addValue( 10, "schools" ,  "90" );
        dataset.addValue( 100, "schools" , "100" );
        return dataset;
    }
}