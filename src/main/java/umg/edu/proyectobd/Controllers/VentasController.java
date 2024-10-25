package umg.edu.proyectobd.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import umg.edu.proyectobd.DB.DatabaseConnection;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class VentasController implements Initializable {

    @FXML
    private TextField campoVentaID;
    @FXML
    private ComboBox<String> comboCliente;
    @FXML
    private TextField campoFechaVenta;
    @FXML
    private ComboBox<String> comboProducto;
    @FXML
    private TextField campoCantidad;
    @FXML
    private TextField campoPrecioUnitario;
    @FXML
    private TextField campoTotal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClientes();
        loadProductos();
        campoFechaVenta.setText(LocalDate.now().toString());
    }

    private void loadClientes() {
        ObservableList<String> clientes = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ClienteID, Cliente_Nombre FROM dbo.Cliente";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    clientes.add(resultSet.getInt("ClienteID") + " - " + resultSet.getString("Cliente_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboCliente.setItems(clientes);
    }

    private void loadProductos() {
        ObservableList<String> productos = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ProductoID, Producto_Nombre FROM dbo.Producto";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    productos.add(resultSet.getInt("ProductoID") + " - " + resultSet.getString("Producto_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboProducto.setItems(productos);
    }

    @FXML
    private void handleCantidadChanged() {
        calculateTotal();
    }

    @FXML
    private void handleProductoSeleccionado() {
        String producto = comboProducto.getValue();
        if (producto != null && !producto.isEmpty()) {
            int productoID = Integer.parseInt(producto.split(" - ")[0]);
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT Producto_CostoUnitario, Producto_Descuento FROM dbo.Producto WHERE ProductoID = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, productoID);
                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        BigDecimal costoUnitario = resultSet.getBigDecimal("Producto_CostoUnitario");
                        BigDecimal descuento = resultSet.getBigDecimal("Producto_Descuento");
                        BigDecimal precioConAumento = costoUnitario.multiply(BigDecimal.valueOf(1.20));
                        BigDecimal precioFinal = precioConAumento.subtract(descuento);
                        campoPrecioUnitario.setText(precioFinal.toString());
                        calculateTotal();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateTotal() {
        String cantidadText = campoCantidad.getText();
        String precioUnitarioText = campoPrecioUnitario.getText();

        if (!cantidadText.isEmpty() && !precioUnitarioText.isEmpty()) {
            try {
                int cantidad = Integer.parseInt(cantidadText);
                BigDecimal precioUnitario = new BigDecimal(precioUnitarioText);
                BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
                campoTotal.setText(total.toString());
            } catch (NumberFormatException e) {
                campoTotal.setText("");
            }
        } else {
            campoTotal.setText("");
        }
    }

    @FXML
    private void handleAddVenta() {
        String cliente = comboCliente.getValue();
        String fechaVenta = campoFechaVenta.getText();
        String total = campoTotal.getText();
        String producto = comboProducto.getValue();
        String cantidad = campoCantidad.getText();
        String precioUnitario = campoPrecioUnitario.getText();

        if (cliente.isEmpty() || fechaVenta.isEmpty() || total.isEmpty() || producto.isEmpty() || cantidad.isEmpty() || precioUnitario.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Porfavor llene todos los campos requeridos.");
            alert.showAndWait();
            return;
        }

        int clienteID = Integer.parseInt(cliente.split(" - ")[0]);
        int productoID = Integer.parseInt(producto.split(" - ")[0]);

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String insertVentaQuery = "INSERT INTO dbo.Venta (ClienteID, Venta_FechaVenta, Venta_Total) VALUES (?, ?, ?)";
            try (PreparedStatement insertVentaStmt = connection.prepareStatement(insertVentaQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertVentaStmt.setInt(1, clienteID);
                insertVentaStmt.setString(2, fechaVenta);
                insertVentaStmt.setBigDecimal(3, new BigDecimal(total));
                insertVentaStmt.executeUpdate();

                ResultSet generatedKeys = insertVentaStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int ventaID = generatedKeys.getInt(1);

                    String insertDetalleQuery = "INSERT INTO dbo.DetalleVenta (VentaID, ProductoID, DetalleVenta_Cantidad, DetalleVenta_PrecioUnitario) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertDetalleStmt = connection.prepareStatement(insertDetalleQuery)) {
                        insertDetalleStmt.setInt(1, ventaID);
                        insertDetalleStmt.setInt(2, productoID);
                        insertDetalleStmt.setInt(3, Integer.parseInt(cantidad));
                        insertDetalleStmt.setBigDecimal(4, new BigDecimal(precioUnitario));
                        insertDetalleStmt.executeUpdate();
                    }
                }

                connection.commit();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Venta agregada exitosamente!");
                alert.showAndWait();

                // Clear all fields
                campoVentaID.clear();
                comboCliente.setValue(null);
                campoFechaVenta.clear();
                campoTotal.clear();
                comboProducto.setValue(null);
                campoCantidad.clear();
                campoPrecioUnitario.clear();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnToMenu() {
        try {
            Stage currentStage = (Stage) campoFechaVenta.getScene().getWindow(); // Use an existing field
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