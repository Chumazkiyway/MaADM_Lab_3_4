package lab;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
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

            System.out.println(solver.getBayesLaplaceCriterion());
            System.out.println(solver.getHermeierCriterion());
            System.out.println(solver.getHodgeLehmannCriterion());
            System.out.println(solver.getHurwitzCriterion());
            System.out.println(solver.getLaplaceCriterion());
            System.out.println(solver.getMiniMaxCriterion());
            System.out.println(solver.getMostProbableResultCriterion());
            System.out.println(solver.getSavageCriterion());
            if (solver.containsNegative()) {
                System.out.println(solver.getMultiplicationCriterion(solver.getChangedWithNegativeMatrixOfProfits()));
            } else {
                System.out.println(solver.getMultiplicationCriterion(solver.getMatrixOfProfits()));
            }
        });

    }

    private List<TableColumn<double[], Integer>> createColumns() {
        List<TableColumn<double[], Integer>> list = new ArrayList<>();
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
        for(int i = 0; i< Solver.COUNT; i++)
            list.add(matrix[i]);
        return list;
    }
}
