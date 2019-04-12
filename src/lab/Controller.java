package lab;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {

    public TextField txt_C1;
    public TextField txt_C2;
    public TextField txt_C3;
    public TextField txt_n1;
    public TextField txt_n2;
    public TextField txt_alpha;
    public TableView<double[]> table;
    public Button btn_Go;
    public ScrollPane scrollPane;
    public Label lbl_MiniMaxCriterion;
    public Label lbl_LaplaceCriterion;
    public Label lbl_SavageCriterion;
    public Label lbl_HurwitzCriterion;
    public Label lbl_MultiplicationCriterion;
    public Label lbl_BayesLaplaceCriterion;
    public Label lbl_HodgeLehmannCriterion;
    public Label lbl_HermeierCriterion;
    public Label lbl_MostProbableResultCriterion;
    private Solver solver;
    @FXML
    void initialize(){

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        btn_Go.setOnMouseClicked(e -> {
            //init params
            solver = new Solver(Double.valueOf(txt_C1.getText()), Double.valueOf(txt_C2.getText()),
                    Double.valueOf(txt_C3.getText()) ,Integer.valueOf(txt_n1.getText()),
                    Integer.valueOf(txt_n2.getText()), Double.valueOf(txt_alpha.getText()));

            table.getItems().clear();
            table.getColumns().setAll(createColumns());

            table.getItems().setAll(getObservableMatrix(solver.getMatrixOfProfits()));

            lbl_BayesLaplaceCriterion.setText("BayesLaplaceCriterion");
            lbl_BayesLaplaceCriterion.setText(lbl_BayesLaplaceCriterion.getText() + ": " + solver.getBayesLaplaceCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_HermeierCriterion.setText("HermeierCriterion");
            lbl_HermeierCriterion.setText(lbl_HermeierCriterion.getText() + ": " + solver.getHermeierCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_HodgeLehmannCriterion.setText("HodgeLehmannCriterion");
            lbl_HodgeLehmannCriterion.setText(lbl_HodgeLehmannCriterion.getText() + ": " + solver.getHodgeLehmannCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_HurwitzCriterion.setText("HurwitzCriterion");
            lbl_HurwitzCriterion.setText(lbl_HurwitzCriterion.getText() + ": " + solver.getHurwitzCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_LaplaceCriterion.setText("LaplaceCriterion");
            lbl_LaplaceCriterion.setText(lbl_LaplaceCriterion.getText() + ": " + solver.getLaplaceCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_MiniMaxCriterion.setText("MiniMaxCriterion");
            lbl_MiniMaxCriterion.setText(lbl_MiniMaxCriterion.getText() + ": " + solver.getMiniMaxCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_MostProbableResultCriterion.setText("MostProbableResultCriterion");
            lbl_MostProbableResultCriterion.setText(lbl_MostProbableResultCriterion.getText() + ": " + solver.getMostProbableResultCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_SavageCriterion.setText("SavageCriterion");
            lbl_SavageCriterion.setText(lbl_SavageCriterion.getText() + ": " + solver.getSavageCriterion() + " i:" + solver.getOptimalCountOfPassengers());

            lbl_MultiplicationCriterion.setText("MultiplicationCriterion");
            if (solver.containsNegative()) {
                lbl_MultiplicationCriterion.setText(lbl_MultiplicationCriterion.getText() + ": " + solver.getMultiplicationCriterion(solver.getChangedWithNegativeMatrixOfProfits()) + " i:" + solver.getOptimalCountOfPassengers());
            } else {
                lbl_MultiplicationCriterion.setText(lbl_MultiplicationCriterion.getText() + ": " + solver.getMultiplicationCriterion(solver.getMatrixOfProfits()) + " i:" + solver.getOptimalCountOfPassengers());
            }
        });

    }

    private List<TableColumn<double[], Integer>> createColumns() {
        List<TableColumn<double[], Integer>> list = new ArrayList<>();

//        list.add(createColumn(Solver.COUNT, 0));

        for(int i = 0; i< solver.getCountOfBusses().length; i++){
            list.add(createColumn(solver.getCountOfBusses()[i], i));
        }
        return list;
    }

    private TableColumn<double[], Integer> createColumn(int c, int i) {
        TableColumn<double[], Integer> col = new TableColumn<>(String.valueOf(c));
        col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>((int) param.getValue()[i]));
        return col;
    }

    private ObservableList<double[]> getObservableMatrix(double[][] matrix) {
        ObservableList<double[]> list = FXCollections.observableArrayList();

//        list.add(solver.getCountOfPassengers());

        for(int i = 0; i< Solver.COUNT; i++)
            list.add(matrix[i]);
        return list;
    }
}
