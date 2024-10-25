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

public class ClientesController {

    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoDireccion;
    @FXML
    private TextField campoTelefono;
    @FXML
    private TextField campoEmail;

    @FXML
    private void handleSearchCliente() {
        String nombre = campoNombre.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor ingrese un nombre de Cliente para buscar.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM dbo.Cliente WHERE Cliente_Nombre = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, nombre);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    campoDireccion.setText(resultSet.getString("Cliente_Direccion"));
                    campoTelefono.setText(resultSet.getString("Cliente_Telefono"));
                    campoEmail.setText(resultSet.getString("Cliente_Email"));
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontró Cliente con el nombre: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateCliente() {
        String nombre = campoNombre.getText();
        String direccion = campoDireccion.getText();
        String telefono = campoTelefono.getText();
        String email = campoEmail.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor ingrese un nombre de Cliente para actualizar.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE dbo.Cliente SET Cliente_Direccion = ?, Cliente_Telefono = ?, Cliente_Email = ? WHERE Cliente_Nombre = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setString(1, direccion);
                updateStmt.setString(2, telefono);
                updateStmt.setString(3, email);
                updateStmt.setString(4, nombre);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hecho");
                    alert.setHeaderText(null);
                    alert.setContentText("Cliente actualizado exitosamente!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontró Cliente con el nombre: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCliente() {
        String nombre = campoNombre.getText();

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor ingrese un nombre de Cliente para eliminar.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteQuery = "DELETE FROM dbo.Cliente WHERE Cliente_Nombre = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, nombre);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hecho");
                    alert.setHeaderText(null);
                    alert.setContentText("Cliente eliminado exitosamente!");
                    alert.showAndWait();

                    // Clear all fields
                    campoNombre.clear();
                    campoDireccion.clear();
                    campoTelefono.clear();
                    campoEmail.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontró Cliente con el nombre: " + nombre);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddCliente() {
        String nombre = campoNombre.getText();
        String direccion = campoDireccion.getText();
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
            String insertQuery = "INSERT INTO dbo.Cliente (Cliente_Nombre, Cliente_Direccion, Cliente_Telefono, Cliente_Email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, nombre);
                insertStmt.setString(2, direccion);
                insertStmt.setString(3, telefono);
                insertStmt.setString(4, email);
                insertStmt.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hecho");
                alert.setHeaderText(null);
                alert.setContentText("Cliente agregado exitosamente!");
                alert.showAndWait();

                // Clear all fields
                campoNombre.clear();
                campoDireccion.clear();
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