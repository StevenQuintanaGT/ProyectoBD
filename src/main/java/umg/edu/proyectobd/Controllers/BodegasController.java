package umg.edu.proyectobd.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import umg.edu.proyectobd.DB.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class BodegasController {

    @FXML
    private TextField campoBodegaID;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoUbicacion;
    @FXML
    private TextField campoMunicipioID;

    @FXML
    private void handleSearchBodega() {
        String nombre = campoNombre.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Pon un nombre de bodega para buscar.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM dbo.Bodega WHERE Bodega_Nombre = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, nombre);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    campoBodegaID.setText(String.valueOf(resultSet.getInt("BodegaID")));
                    campoUbicacion.setText(resultSet.getString("Bodega_Ubicacion"));
                    campoMunicipioID.setText(String.valueOf(resultSet.getInt("MunicipioID")));

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Bodega Encontrada");
                    alert.setHeaderText(null);
                    alert.setContentText("Bodega Encontrada y campos llenados");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Bodega No Encontrada");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontro una bodega con el nombre: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateBodega() {
        String bodegaID = campoBodegaID.getText();
        String nombre = campoNombre.getText();
        String ubicacion = campoUbicacion.getText();
        String municipioID = campoMunicipioID.getText();

        if (bodegaID.isEmpty() || nombre.isEmpty() || municipioID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor llena todos los campos requeridos.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE dbo.Bodega SET Bodega_Nombre = ?, Bodega_Ubicacion = ?, MunicipioID = ? WHERE BodegaID = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setString(1, nombre);
                updateStmt.setString(2, ubicacion);
                updateStmt.setInt(3, Integer.parseInt(municipioID));
                updateStmt.setInt(4, Integer.parseInt(bodegaID));
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hecho");
                    alert.setHeaderText(null);
                    alert.setContentText("Bodega actualizada exitosamente!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontro la bodega con el ID: " + bodegaID);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteBodega() {
        String bodegaID = campoBodegaID.getText();

        if (bodegaID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor llena el campo de ID de Bodega.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteQuery = "DELETE FROM dbo.Bodega WHERE BodegaID = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, Integer.parseInt(bodegaID));
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hecho");
                    alert.setHeaderText(null);
                    alert.setContentText("Bodega borrada exitosamente!");
                    alert.showAndWait();

                    // Clear all fields
                    campoBodegaID.clear();
                    campoNombre.clear();
                    campoUbicacion.clear();
                    campoMunicipioID.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontro una bodega con el ID: " + bodegaID);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBodega() {
        String nombre = campoNombre.getText();
        String ubicacion = campoUbicacion.getText();
        String municipioID = campoMunicipioID.getText();

        if (nombre.isEmpty() || municipioID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor llena todos los campos requeridos.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO dbo.Bodega (Bodega_Nombre, Bodega_Ubicacion, MunicipioID) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, nombre);
                insertStmt.setString(2, ubicacion);
                insertStmt.setInt(3, Integer.parseInt(municipioID));
                insertStmt.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hecho");
                alert.setHeaderText(null);
                alert.setContentText("Bodega agregada exitosamente!");
                alert.showAndWait();

                // Clear all fields
                campoBodegaID.clear();
                campoNombre.clear();
                campoUbicacion.clear();
                campoMunicipioID.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnToMenu() {
        try {
            Stage currentStage = (Stage) campoBodegaID.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/main_menu.fxml")));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}