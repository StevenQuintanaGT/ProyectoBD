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

public class ProveedoresController {

    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoContacto;
    @FXML
    private TextField campoTelefono;
    @FXML
    private TextField campoEmail;

    @FXML
    private void handleSearchProveedor() {
        String nombre = campoNombre.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a Proveedor name to search.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM dbo.Proveedor WHERE Proveedor_Nombre = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, nombre);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    campoContacto.setText(resultSet.getString("Proveedor_Contacto"));
                    campoTelefono.setText(resultSet.getString("Proveedor_Telefono"));
                    campoEmail.setText(resultSet.getString("Proveedor_Email"));
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No Proveedor found with name: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateProveedor() {
        String nombre = campoNombre.getText();
        String contacto = campoContacto.getText();
        String telefono = campoTelefono.getText();
        String email = campoEmail.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE dbo.Proveedor SET Proveedor_Contacto = ?, Proveedor_Telefono = ?, Proveedor_Email = ? WHERE Proveedor_Nombre = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setString(1, contacto);
                updateStmt.setString(2, telefono);
                updateStmt.setString(3, email);
                updateStmt.setString(4, nombre);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Proveedor updated successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No Proveedor found with name: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteProveedor() {
        String nombre = campoNombre.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a Proveedor name to delete.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteQuery = "DELETE FROM dbo.Proveedor WHERE Proveedor_Nombre = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, nombre);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Proveedor deleted successfully!");
                    alert.showAndWait();

                    // Clear all fields
                    campoNombre.clear();
                    campoContacto.clear();
                    campoTelefono.clear();
                    campoEmail.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No Proveedor found with name: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddProveedor() {
        String nombre = campoNombre.getText();
        String contacto = campoContacto.getText();
        String telefono = campoTelefono.getText();
        String email = campoEmail.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO dbo.Proveedor (Proveedor_Nombre, Proveedor_Contacto, Proveedor_Telefono, Proveedor_Email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, nombre);
                insertStmt.setString(2, contacto);
                insertStmt.setString(3, telefono);
                insertStmt.setString(4, email);
                insertStmt.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Proveedor added successfully!");
                alert.showAndWait();

                // Clear all fields
                campoNombre.clear();
                campoContacto.clear();
                campoTelefono.clear();
                campoEmail.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnToMenu() {
        try {
            Stage currentStage = (Stage) campoNombre.getScene().getWindow();
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