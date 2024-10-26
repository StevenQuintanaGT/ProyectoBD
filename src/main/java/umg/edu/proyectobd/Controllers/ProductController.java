
package umg.edu.proyectobd.Controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import umg.edu.proyectobd.DB.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML
    private TextField campoSku;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoDescripcion;
    @FXML
    private ComboBox<String> comboBoxTipo;
    @FXML
    private ComboBox<String> comboBoxCategoria;
    @FXML
    private ComboBox<String> comboBoxUnidadMedida;
    @FXML
    private TextField campoCostoUnitario;
    @FXML
    private TextField campoDescuento;
    @FXML
    private TextField campoImagen;
    @FXML
    private TextField campoPeso;
    @FXML
    private ComboBox<String> comboBoxBodega;
    @FXML
    private ComboBox<String> comboProveedor;
    @FXML
    private DatePicker fechaSuministro;
    @FXML
    private TextField campoCantidad;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTipos();
        loadCategorias();
        loadUnidadesMedida();
        loadBodegas();
        loadProveedores();
        fechaSuministro.setValue(LocalDate.now());
    }


    private void loadTipos() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT Tipo_Nombre FROM dbo.Tipo";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(query);
                while (resultSet.next()) {
                    comboBoxTipo.getItems().add(resultSet.getString("Tipo_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCategorias() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT Categoria_Nombre FROM dbo.Categoria";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(query);
                while (resultSet.next()) {
                    comboBoxCategoria.getItems().add(resultSet.getString("Categoria_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUnidadesMedida() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT UnidadMedida_Nombre FROM dbo.UnidadMedida";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(query);
                while (resultSet.next()) {
                    comboBoxUnidadMedida.getItems().add(resultSet.getString("UnidadMedida_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBodegas() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT Bodega_Nombre FROM dbo.Bodega";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(query);
                while (resultSet.next()) {
                    comboBoxBodega.getItems().add(resultSet.getString("Bodega_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProveedores() {
        ObservableList<String> proveedores = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ProveedorID, Proveedor_Nombre FROM dbo.Proveedor";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    proveedores.add(resultSet.getInt("ProveedorID") + resultSet.getString("Proveedor_Nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboProveedor.setItems(proveedores);
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            campoImagen.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleReturnToMenu(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) campoSku.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/main_menu.fxml")));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        currentStage.close();
    }

@FXML
private void handleAddProduct() {
    String sku = campoSku.getText();
    String nombre = campoNombre.getText();
    String descripcion = campoDescripcion.getText();
    String tipo = comboBoxTipo.getValue();
    String categoria = comboBoxCategoria.getValue();
    String unidadMedida = comboBoxUnidadMedida.getValue();
    String costoUnitario = campoCostoUnitario.getText();
    String descuento = campoDescuento.getText();
    String imagen = campoImagen.getText();
    String peso = campoPeso.getText();
    String bodega = comboBoxBodega.getValue();
    String proveedor = comboProveedor.getValue();
    LocalDate fecha = fechaSuministro.getValue();
    String cantidad = campoCantidad.getText();

    if (sku.isEmpty() || nombre.isEmpty() || tipo == null || categoria == null || costoUnitario.isEmpty() || bodega == null || proveedor == null || fecha == null || cantidad.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Por favor llena todos los campos requeridos.");
        alert.showAndWait();
        return;
    }

    try (Connection connection = DatabaseConnection.getConnection()) {
        connection.setAutoCommit(false);

        int categoriaID = getCategoriaID(categoria);
        int proveedorID = getProveedorID(proveedor);

        String insertProductoQuery = "INSERT INTO dbo.Producto (Producto_SKU, Producto_Nombre, Producto_Descripcion, TipoID, CategoriaID, UnidadMedidaID, Producto_CostoUnitario, Producto_Descuento, Producto_Imagen, Producto_Peso, BodegaID, Producto_Cantidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertProductoStmt = connection.prepareStatement(insertProductoQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertProductoStmt.setString(1, sku);
            insertProductoStmt.setString(2, nombre);
            insertProductoStmt.setString(3, descripcion);
            insertProductoStmt.setInt(4, getTipoID(tipo));
            insertProductoStmt.setInt(5, categoriaID);
            insertProductoStmt.setInt(6, getUnidadMedidaID(unidadMedida));
            insertProductoStmt.setBigDecimal(7, new BigDecimal(costoUnitario));
            insertProductoStmt.setBigDecimal(8, new BigDecimal(descuento));
            insertProductoStmt.setString(9, imagen);
            insertProductoStmt.setBigDecimal(10, new BigDecimal(peso));
            insertProductoStmt.setInt(11, getBodegaID(bodega));
            insertProductoStmt.setInt(12, Integer.parseInt(cantidad));
            insertProductoStmt.executeUpdate();

            ResultSet generatedKeys = insertProductoStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int productoID = generatedKeys.getInt(1);

                String insertProductoProveedorQuery = "INSERT INTO dbo.ProductoProveedor (ProductoID, ProveedorID, ProductoProveedor_FechaSuministro) VALUES (?, ?, ?)";
                try (PreparedStatement insertProductoProveedorStmt = connection.prepareStatement(insertProductoProveedorQuery)) {
                    insertProductoProveedorStmt.setInt(1, productoID);
                    insertProductoProveedorStmt.setInt(2, proveedorID);
                    insertProductoProveedorStmt.setDate(3, Date.valueOf(fecha));
                    insertProductoProveedorStmt.executeUpdate();
                }
            } else {
                throw new SQLException("Error al insertar Producto: " + sku);
            }

            connection.commit();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hecho");
            alert.setHeaderText(null);
            alert.setContentText("Producto agregado exitosamente!");
            alert.showAndWait();

            clearFields();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } catch (NumberFormatException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Formato de número inválido en uno de los campos.");
        alert.showAndWait();
    }
}

private int getProveedorID(String proveedor) throws SQLException {
    String query = "SELECT ProveedorID FROM dbo.Proveedor WHERE Proveedor_Nombre = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, proveedor);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("ProveedorID");
        } else {
            throw new SQLException("Proveedor no encontrado: " + proveedor);
        }
    }
}


@FXML
private void handleUpdateProduct() {
    Logger logger = Logger.getLogger(ProductController.class.getName());

    String sku = campoSku.getText();
    String nombre = campoNombre.getText();
    String descripcion = campoDescripcion.getText();
    String tipo = comboBoxTipo.getValue();
    String categoria = comboBoxCategoria.getValue();
    String unidadMedida = comboBoxUnidadMedida.getValue();
    String costoUnitario = campoCostoUnitario.getText();
    String descuento = campoDescuento.getText();
    String imagen = campoImagen.getText();
    String peso = campoPeso.getText();
    String bodega = comboBoxBodega.getValue();
    String proveedor = comboProveedor.getValue();
    LocalDate fecha = fechaSuministro.getValue();
    String cantidad = campoCantidad.getText();

    logger.log(Level.INFO, "Se ejecuta el método handleUpdateProduct con SKU: {0}", sku);

    if (sku.isEmpty() || nombre.isEmpty() || tipo == null || categoria == null || costoUnitario.isEmpty() || bodega == null || proveedor == null || fecha == null || cantidad.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Por favor llena todos los campos requeridos.");
        alert.showAndWait();
        return;
    }

    try {
        int cantidadInt = Integer.parseInt(cantidad);

        logger.log(Level.INFO, "Parsed cantidad: {0}", cantidadInt);

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String updateProductoQuery = "UPDATE dbo.Producto SET Producto_Nombre = ?, Producto_Descripcion = ?, TipoID = ?, CategoriaID = ?, UnidadMedidaID = ?, Producto_CostoUnitario = ?, Producto_Descuento = ?, Producto_Imagen = ?, Producto_Peso = ?, BodegaID = ?, Producto_Cantidad = ? WHERE Producto_SKU = ?";
            try (PreparedStatement updateProductoStmt = connection.prepareStatement(updateProductoQuery)) {
                updateProductoStmt.setString(1, nombre);
                updateProductoStmt.setString(2, descripcion);
                updateProductoStmt.setInt(3, getTipoID(tipo));
                updateProductoStmt.setInt(4, getCategoriaID(categoria));
                updateProductoStmt.setInt(5, getUnidadMedidaID(unidadMedida));
                updateProductoStmt.setBigDecimal(6, new BigDecimal(costoUnitario));
                updateProductoStmt.setBigDecimal(7, new BigDecimal(descuento));
                updateProductoStmt.setString(8, imagen);
                updateProductoStmt.setBigDecimal(9, new BigDecimal(peso));
                updateProductoStmt.setInt(10, getBodegaID(bodega));
                updateProductoStmt.setInt(11, cantidadInt);
                updateProductoStmt.setString(12, sku);
                updateProductoStmt.executeUpdate();

                logger.log(Level.INFO, "Updated Producto con SKU: {0}", sku);

                String updateProductoProveedorQuery = "UPDATE dbo.ProductoProveedor SET ProveedorID = (SELECT ProveedorID FROM dbo.Proveedor WHERE Proveedor_Nombre = ?), ProductoProveedor_FechaSuministro = ? WHERE ProductoID = (SELECT ProductoID FROM dbo.Producto WHERE Producto_SKU = ?)";
                try (PreparedStatement updateProductoProveedorStmt = connection.prepareStatement(updateProductoProveedorQuery)) {
                    updateProductoProveedorStmt.setString(1, proveedor);
                    updateProductoProveedorStmt.setDate(2, Date.valueOf(fecha));
                    updateProductoProveedorStmt.setString(3, sku);
                    updateProductoProveedorStmt.executeUpdate();

                    logger.log(Level.INFO, "Updated ProductoProveedor para SKU: {0}", sku);
                }

                connection.commit();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hecho");
                alert.setHeaderText(null);
                alert.setContentText("Producto actualizado exitosamente!");
                alert.showAndWait();

                clearFields();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception: ", e);
            e.printStackTrace();
        }
    } catch (NumberFormatException e) {
        logger.log(Level.SEVERE, "Number Format Exception: ", e);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Formato de número inválido en el campo Cantidad.");
        alert.showAndWait();
    }
}

    @FXML
    private void handleDeleteProduct() {
        String sku = campoSku.getText();
        if (sku.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Por favor ingresa el SKU del producto a eliminar.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteProductoProveedorQuery = "DELETE FROM dbo.ProductoProveedor WHERE ProductoID = (SELECT ProductoID FROM dbo.Producto WHERE Producto_SKU = ?)";
            try (PreparedStatement deleteProductoProveedorStmt = connection.prepareStatement(deleteProductoProveedorQuery)) {
                deleteProductoProveedorStmt.setString(1, sku);
                deleteProductoProveedorStmt.executeUpdate();
            }

            String deleteProductoQuery = "DELETE FROM dbo.Producto WHERE Producto_SKU = ?";
            try (PreparedStatement deleteProductoStmt = connection.prepareStatement(deleteProductoQuery)) {
                deleteProductoStmt.setString(1, sku);
                int rowsAffected = deleteProductoStmt.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hecho");
                    alert.setHeaderText(null);
                    alert.setContentText("Producto eliminado exitosamente!");
                    alert.showAndWait();
                    clearFields();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Producto no encontrado con SKU: " + sku);
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private String getTipoNombre(int tipoID) throws SQLException {
        String query = "SELECT Tipo_Nombre FROM dbo.Tipo WHERE TipoID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tipoID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Tipo_Nombre");
            } else {
                throw new SQLException("Tipo not found for ID: " + tipoID);
            }
        }
    }

    private String getCategoriaNombre(int categoriaID) throws SQLException {
        String query = "SELECT Categoria_Nombre FROM dbo.Categoria WHERE CategoriaID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoriaID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Categoria_Nombre");
            } else {
                throw new SQLException("Categoria not found for ID: " + categoriaID);
            }
        }
    }

    private String getUnidadMedidaNombre(int unidadMedidaID) throws SQLException {
        String query = "SELECT UnidadMedida_Nombre FROM dbo.UnidadMedida WHERE UnidadMedidaID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, unidadMedidaID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("UnidadMedida_Nombre");
            } else {
                throw new SQLException("UnidadMedida not found for ID: " + unidadMedidaID);
            }
        }
    }

    private String getBodegaNombre(int bodegaID) throws SQLException {
        String query = "SELECT Bodega_Nombre FROM dbo.Bodega WHERE BodegaID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bodegaID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Bodega_Nombre");
            } else {
                throw new SQLException("Bodega not found for ID: " + bodegaID);
            }
        }
    }

    private String getProveedorNombre(int proveedorID) throws SQLException {
        String proveedorNombre = "";
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT Proveedor_Nombre FROM dbo.Proveedor WHERE ProveedorID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, proveedorID);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    proveedorNombre = resultSet.getString("Proveedor_Nombre");
                }
            }
        }
        return proveedorNombre;
    }

    private int getTipoID(String tipoNombre) throws SQLException {
        String query = "SELECT TipoID FROM dbo.Tipo WHERE Tipo_Nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, tipoNombre);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("TipoID");
            } else {
                throw new SQLException("Tipo not found: " + tipoNombre);
            }
        }
    }

    private int getCategoriaID(String categoriaNombre) throws SQLException {
        String query = "SELECT CategoriaID FROM dbo.Categoria WHERE Categoria_Nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, categoriaNombre);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("CategoriaID");
            } else {
                // Insert new Categoria if not found
                String insertQuery = "INSERT INTO dbo.Categoria (Categoria_Nombre, Categoria_Descripcion) VALUES (?, '')";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, categoriaNombre);
                    insertStmt.executeUpdate();
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert new Categoria: " + categoriaNombre);
                    }
                }
            }
        }
    }

    private int getUnidadMedidaID(String unidadMedidaNombre) throws SQLException {
        String query = "SELECT UnidadMedidaID FROM dbo.UnidadMedida WHERE UnidadMedida_Nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, unidadMedidaNombre);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("UnidadMedidaID");
            } else {
                // Insert new UnidadMedida if not found
                String insertQuery = "INSERT INTO dbo.UnidadMedida (UnidadMedida_Nombre) VALUES (?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, unidadMedidaNombre);
                    insertStmt.executeUpdate();
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert new UnidadMedida: " + unidadMedidaNombre);
                    }
                }
            }
        }
    }

    private int getBodegaID(String bodegaNombre) throws SQLException {
        String query = "SELECT BodegaID FROM dbo.Bodega WHERE Bodega_Nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bodegaNombre);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("BodegaID");
            } else {
                throw new SQLException("Bodega not found: " + bodegaNombre);
            }
        }
    }

    private void clearFields() {
        campoSku.clear();
        campoNombre.clear();
        campoDescripcion.clear();
        comboBoxTipo.setValue(null);
        comboBoxCategoria.setValue(null);
        comboBoxUnidadMedida.setValue(null);
        campoCostoUnitario.clear();
        campoDescuento.clear();
        campoImagen.clear();
        campoPeso.clear();
        comboBoxBodega.setValue(null);
        comboProveedor.setValue(null);
        fechaSuministro.setValue(LocalDate.now());
        campoCantidad.clear();
    }

    @FXML
private void handleSearchProduct(ActionEvent event) {
    String sku = campoSku.getText();
    if (sku.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Porfavor ingresa el SKU del producto a buscar.");
        alert.showAndWait();
        return;
    }

    String query = "SELECT p.*, pp.ProveedorID, pp.ProductoProveedor_FechaSuministro FROM dbo.Producto p LEFT JOIN dbo.ProductoProveedor pp ON p.ProductoID = pp.ProductoID WHERE p.Producto_SKU = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, sku);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            campoNombre.setText(resultSet.getString("Producto_Nombre"));
            campoDescripcion.setText(resultSet.getString("Producto_Descripcion"));
            comboBoxTipo.setValue(getTipoNombre(resultSet.getInt("TipoID")));
            comboBoxCategoria.setValue(getCategoriaNombre(resultSet.getInt("CategoriaID")));
            comboBoxUnidadMedida.setValue(getUnidadMedidaNombre(resultSet.getInt("UnidadMedidaID")));
            campoCostoUnitario.setText(resultSet.getBigDecimal("Producto_CostoUnitario").toString());
            campoDescuento.setText(resultSet.getBigDecimal("Producto_Descuento").toString());
            campoImagen.setText(resultSet.getString("Producto_Imagen"));
            campoPeso.setText(resultSet.getBigDecimal("Producto_Peso").toString());
            comboBoxBodega.setValue(getBodegaNombre(resultSet.getInt("BodegaID")));
            comboProveedor.setValue(getProveedorNombre(resultSet.getInt("ProveedorID")));
            fechaSuministro.setValue(resultSet.getDate("ProductoProveedor_FechaSuministro").toLocalDate());
            campoCantidad.setText(String.valueOf(resultSet.getInt("Producto_Cantidad")));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("No se encontró un producto con SKU: " + sku);
            alert.showAndWait();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}