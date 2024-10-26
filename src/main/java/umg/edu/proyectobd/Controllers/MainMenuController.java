package umg.edu.proyectobd.Controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import umg.edu.proyectobd.DB.DatabaseConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Objects;

public class MainMenuController {

    @FXML
    private void handleProductos(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/productos.fxml")));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        currentStage.close();
    }

    @FXML
    private void handleBodegas(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/bodegas.fxml")));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        currentStage.close();
    }

    @FXML
    private void handleClientes(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/clientes.fxml")));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        currentStage.close();
    }

    @FXML
    private void handleProveedores(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/proveedores.fxml")));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        currentStage.close();
    }

    @FXML
    private void handleVentas(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/umg/edu/proyectobd/ventas.fxml")));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        currentStage.close();
    }

    @FXML
    private void handleGenerarReportes() {
        try {
            generateProductReport();
            generateSalesReport();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hecho");
            alert.setHeaderText(null);
            alert.setContentText("Reportes generados exitosamente.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al generar reportes.");
            alert.showAndWait();
        }
    }

    private void generateProductReport() throws DocumentException, IOException, SQLException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("ProductReport.pdf"));
        document.open();

        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        document.add(new Paragraph("AlterShop", boldFont));
        document.add(new Paragraph("Fecha del Reporte: " + LocalDate.now()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(8);
        addTableHeader(table, new String[]{"SKU", "Nombre", "Descripcion", "Tipo", "Costo Unitario", "Cantidad", "Categoria", "Bodega"});

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.Producto_SKU, p.Producto_Nombre, p.Producto_Descripcion, t.Tipo_Nombre, p.Producto_CostoUnitario, p.Producto_Cantidad, c.Categoria_Nombre, b.Bodega_Nombre " +
                     "FROM Producto p " +
                     "INNER JOIN Tipo t ON p.TipoID = t.TipoID " +
                     "INNER JOIN Categoria c ON p.CategoriaID = c.CategoriaID " +
                     "INNER JOIN Bodega b ON p.BodegaID = b.BodegaID")) {

            while (rs.next()) {
                table.addCell(rs.getString("Producto_SKU"));
                table.addCell(rs.getString("Producto_Nombre"));
                table.addCell(rs.getString("Producto_Descripcion"));
                table.addCell(rs.getString("Tipo_Nombre"));
                table.addCell(rs.getString("Producto_CostoUnitario"));
                table.addCell(rs.getString("Producto_Cantidad"));
                table.addCell(rs.getString("Categoria_Nombre"));
                table.addCell(rs.getString("Bodega_Nombre"));
            }
        }

        document.add(table);
        document.close();
    }

    private void generateSalesReport() throws DocumentException, IOException, SQLException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("SalesReport.pdf"));
        document.open();

        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        document.add(new Paragraph("AlterShop", boldFont));
        document.add(new Paragraph("Fecha del Reporte: " + LocalDate.now()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        addTableHeader(table, new String[]{"VentaID", "Cliente", "Fecha Venta", "Total Venta", "Cantidad", "Precio Unitario"});

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT v.VentaID, c.Cliente_Nombre, v.Venta_FechaVenta, v.Venta_Total, dv.DetalleVenta_Cantidad, dv.DetalleVenta_PrecioUnitario " +
                     "FROM Venta v " +
                     "INNER JOIN Cliente c ON v.ClienteID = c.ClienteID " +
                     "INNER JOIN DetalleVenta dv ON v.VentaID = dv.VentaID")) {

            while (rs.next()) {
                table.addCell(rs.getString("VentaID"));
                table.addCell(rs.getString("Cliente_Nombre"));
                table.addCell(rs.getString("Venta_FechaVenta"));
                table.addCell(rs.getString("Venta_Total"));
                table.addCell(rs.getString("DetalleVenta_Cantidad"));
                table.addCell(rs.getString("DetalleVenta_PrecioUnitario"));
            }
        }

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}