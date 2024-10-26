-- Crear la base de datos
--CREATE DATABASE Inventario;
--GO

-- Usar la base de datos
USE Inventario;
GO

-- Crear la tabla Categoria
CREATE TABLE Categoria (
    CategoriaID INT PRIMARY KEY IDENTITY(1,1),
    Categoria_Nombre NVARCHAR(100) NOT NULL UNIQUE,
    Categoria_Descripcion NVARCHAR(255)
);
GO

-- Crear la tabla Departamento
CREATE TABLE Departamento (
    DepartamentoID INT PRIMARY KEY IDENTITY(1,1),
    Departamento_Nombre NVARCHAR(100) NOT NULL UNIQUE
);
GO

-- Crear la tabla Municipio
CREATE TABLE Municipio (
    MunicipioID INT PRIMARY KEY IDENTITY(1,1),
    Municipio_Nombre NVARCHAR(100) NOT NULL UNIQUE,
    DepartamentoID INT NOT NULL,
    CONSTRAINT FK_Municipio_Departamento FOREIGN KEY (DepartamentoID) REFERENCES Departamento(DepartamentoID)
);
GO

-- Crear la tabla Bodega
CREATE TABLE Bodega (
    BodegaID INT PRIMARY KEY IDENTITY(1,1),
    Bodega_Nombre NVARCHAR(100) NOT NULL UNIQUE,
    Bodega_Ubicacion NVARCHAR(255),
    MunicipioID INT NOT NULL,
    CONSTRAINT FK_Bodega_Municipio FOREIGN KEY (MunicipioID) REFERENCES Municipio(MunicipioID)
);
GO

-- Crear la tabla Tipo
CREATE TABLE Tipo (
    TipoID INT PRIMARY KEY IDENTITY(1,1),
    Tipo_Nombre NVARCHAR(50) NOT NULL UNIQUE,
	CONSTRAINT CH_Tipo_Nombre CHECK (Tipo_Nombre IN ('Bien', 'Servicio'))
);
GO

-- Crear la tabla UnidadMedida
CREATE TABLE UnidadMedida (
    UnidadMedidaID INT PRIMARY KEY IDENTITY(1,1),
    UnidadMedida_Nombre NVARCHAR(50) NOT NULL UNIQUE
);
GO

-- Crear la tabla Producto
CREATE TABLE Producto (
    ProductoID INT PRIMARY KEY IDENTITY(1,1),
    Producto_SKU NVARCHAR(50) NOT NULL UNIQUE,
    Producto_Nombre NVARCHAR(100) NOT NULL,
    Producto_Descripcion NVARCHAR(255),
    TipoID INT NOT NULL,
    Producto_CostoUnitario DECIMAL(18, 2) NOT NULL,
    UnidadMedidaID INT NOT NULL,
    Producto_Descuento DECIMAL(5, 2),
    Producto_Imagen NVARCHAR(MAX),
    Producto_Peso DECIMAL(10, 2),
	Producto_Cantidad INT NOT NULL,
    CategoriaID INT NOT NULL,
    BodegaID INT NOT NULL,
    CONSTRAINT FK_Producto_Categoria FOREIGN KEY (CategoriaID) REFERENCES Categoria(CategoriaID),
    CONSTRAINT FK_Producto_Bodega FOREIGN KEY (BodegaID) REFERENCES Bodega(BodegaID),
    CONSTRAINT FK_Producto_Tipo FOREIGN KEY (TipoID) REFERENCES Tipo(TipoID),
    CONSTRAINT FK_Producto_UnidadMedida FOREIGN KEY (UnidadMedidaID) REFERENCES UnidadMedida(UnidadMedidaID)
);
GO

-- Crear la tabla Proveedor
CREATE TABLE Proveedor (
    ProveedorID INT PRIMARY KEY IDENTITY(1,1),
    Proveedor_Nombre NVARCHAR(100) NOT NULL UNIQUE,
    Proveedor_Contacto NVARCHAR(100),
    Proveedor_Telefono NVARCHAR(20),
    Proveedor_Email NVARCHAR(100)
);
GO

-- Crear la tabla ProductoProveedor
CREATE TABLE ProductoProveedor (
    ProductoID INT NOT NULL,
    ProveedorID INT NOT NULL,
    ProductoProveedor_FechaSuministro DATE NOT NULL,
    CONSTRAINT PK_ProductoProveedor PRIMARY KEY (ProductoID, ProveedorID),
    CONSTRAINT FK_ProductoProveedor_Producto FOREIGN KEY (ProductoID) REFERENCES Producto(ProductoID),
    CONSTRAINT FK_ProductoProveedor_Proveedor FOREIGN KEY (ProveedorID) REFERENCES Proveedor(ProveedorID)
);
GO

-- Crear la tabla Cliente
CREATE TABLE Cliente (
    ClienteID INT PRIMARY KEY IDENTITY(1,1),
    Cliente_Nombre NVARCHAR(100) NOT NULL,
    Cliente_Direccion NVARCHAR(255),
    Cliente_Telefono NVARCHAR(20),
    Cliente_Email NVARCHAR(100)
);
GO

-- Crear la tabla Venta
CREATE TABLE Venta (
    VentaID INT PRIMARY KEY IDENTITY(1,1),
    ClienteID INT NOT NULL,
    Venta_FechaVenta DATE NOT NULL,
    Venta_Total DECIMAL(18, 2) NOT NULL,
    CONSTRAINT FK_Venta_Cliente FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID)
);
GO

-- Crear la tabla DetalleVenta
CREATE TABLE DetalleVenta (
    DetalleVentaID INT PRIMARY KEY IDENTITY(1,1),
    VentaID INT NOT NULL,
    ProductoID INT NOT NULL,
    DetalleVenta_Cantidad INT NOT NULL,
    DetalleVenta_PrecioUnitario DECIMAL(18, 2) NOT NULL,
    CONSTRAINT FK_DetalleVenta_Venta FOREIGN KEY (VentaID) REFERENCES Venta(VentaID),
    CONSTRAINT FK_DetalleVenta_Producto FOREIGN KEY (ProductoID) REFERENCES Producto(ProductoID)
);
GO



-- Insertar valores en la tabla Tipo
INSERT INTO Tipo (Tipo_Nombre) VALUES ('Servicio');
INSERT INTO Tipo (Tipo_Nombre) VALUES ('Bien');

-- Insertar los 22 departamentos de Guatemala en la tabla Departamento
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Guatemala');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('El Progreso');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Sacatepéquez');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Chimaltenango');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Escuintla');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Santa Rosa');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Sololá');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Totonicapán');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Quetzaltenango');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Suchitepéquez');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Retalhuleu');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('San Marcos');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Huehuetenango');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Quiché');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Baja Verapaz');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Alta Verapaz');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Petén');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Izabal');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Zacapa');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Chiquimula');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Jalapa');
INSERT INTO Departamento (Departamento_Nombre) VALUES ('Jutiapa');

INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Guatemala', 1); -- Guatemala
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Guastatoya', 2); -- El Progreso
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Antigua Guatemala', 3); -- Sacatepéquez
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Chimaltenango', 4); -- Chimaltenango
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Escuintla', 5); -- Escuintla
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Cuilapa', 6); -- Santa Rosa
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Sololá', 7); -- Sololá
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Totonicapán', 8); -- Totonicapán
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Quetzaltenango', 9); -- Quetzaltenango
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Mazatenango', 10); -- Suchitepéquez
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Retalhuleu', 11); -- Retalhuleu
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('San Marcos', 12); -- San Marcos
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Huehuetenango', 13); -- Huehuetenango
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Santa Cruz del Quiché', 14); -- Quiché
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Salamá', 15); -- Baja Verapaz
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Cobán', 16); -- Alta Verapaz
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Flores', 17); -- Petén
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Puerto Barrios', 18); -- Izabal
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Zacapa', 19); -- Zacapa
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Chiquimula', 20); -- Chiquimula
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Jalapa', 21); -- Jalapa
INSERT INTO Municipio (Municipio_Nombre, DepartamentoID) VALUES ('Jutiapa', 22); -- Jutiapa
