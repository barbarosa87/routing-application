package Annealing;

//<editor-fold defaultstate="collapsed" desc="Imports">
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
//</editor-fold>
public class BarChart extends ApplicationFrame implements ChartMouseListener{
    List <Integer> Cost_List = new ArrayList<Integer>();
    public static PickedSolution p;
    
//<editor-fold defaultstate="collapsed" desc="BarChart constructor">
    public BarChart(java.util.List <Integer> Cost_List){
        super("Annealing Algorithm BarChart");
        this.Cost_List=Cost_List;
        final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);
        chartPanel.addChartMouseListener(this);
        
  }
 //</editor-fold>   
    
//<editor-fold defaultstate="collapsed" desc="Creating dataset">  
private CategoryDataset createDataset() {
    
  

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     for(int i=0;i<Cost_List.size();i++){
        dataset.addValue(Cost_List.get(i), "Cost",String.valueOf(i+1));
     }
        return dataset;
        
}
//</editor-fold>


//<editor-fold defaultstate="collapsed" desc="CreatingChart">
   private JFreeChart createChart(final CategoryDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Annealing Algorithm Bar Chart ",         // chart title
            "Round Number",            
            "Cost ",                  
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
         // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
       
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        return chart;
        
    }
//</editor-fold>
  
   
//<editor-fold defaultstate="collapsed" desc="Events">
   @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
   try {
       CategoryItemEntity entity=(CategoryItemEntity) cme.getEntity();
       String category=entity.getColumnKey().toString();
       p =new PickedSolution(category);
       p.setTitle("Annealing Algorithm Picked Solution");
       p.setSize(new Dimension(700,400));
   }catch(ClassCastException e){ System.out.println(e.toString());
   }
}
    

             
    

    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        
    }
//</editor-fold>
  
}
