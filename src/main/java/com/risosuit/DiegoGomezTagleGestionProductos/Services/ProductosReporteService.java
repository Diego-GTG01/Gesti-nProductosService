package com.risosuit.DiegoGomezTagleGestionProductos.Services;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Reporte;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Departamento;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ProductosReporteService {

    @Value("${reportes.excel.directorio:reportes/productos}")
    private String directorioSalida;

    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String COLOR_HEADER = "1B365D"; // azul corporativo, igual que el PDF
    private static final String[] HEADERS_PRODUCTOS = {
        "ID",
        "Imagen",
        "Folio",
        "Clave",
        "Nombre",
        "Descripción",
        "Departamento",
        "Precio",
        "Status",
        "Registrado por (Nombre)",
        "Usuario (Login)",
        "Email",
        "Celular",
        "Teléfono",
        "Rol",
        "Fecha Registro",
        "Fecha Actualización"
    };

    public Reporte generarReporteProductosExcel(List<Producto> productos) {
        Reporte reporte = new Reporte();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            Estilos estilos = new Estilos(workbook);
            EstadisticasProductos stats = calcularEstadisticas(productos);

            crearHojaProductos(workbook, estilos, productos);
            crearHojaEstadisticas(workbook, estilos, stats);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] excelBytes = bos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(excelBytes);

            String nombreArchivo = "reporte_productos_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".xlsx";

            reporte.setStatus(1);
            reporte.setNombre("Reporte de Productos");
            reporte.setFileName(nombreArchivo);
            reporte.setFile(base64);

            try {
                Path ruta = guardarEnDisco(excelBytes, nombreArchivo);
                reporte.setMessage("Reporte de productos generado. Copia guardada en: " + ruta.toAbsolutePath());
            } catch (IOException ioDisco) {
                reporte.setMessage("Reporte de productos generado correctamente. "
                        + "No se pudo guardar la copia física en disco: " + ioDisco.getMessage());
            }

        } catch (IOException e) {
            reporte.setStatus(0);
            reporte.setMessage("Error al generar el reporte Excel de productos: " + e.getMessage());
        }

        return reporte;
    }

    private void crearHojaProductos(XSSFWorkbook workbook, Estilos estilos, List<Producto> productos) {
        XSSFSheet hoja = workbook.createSheet("Productos");

        Row filaTitulo = hoja.createRow(0);
        Cell tituloCell = filaTitulo.createCell(0);
        tituloCell.setCellValue("LISTADO DE PRODUCTOS");
        tituloCell.setCellStyle(estilos.titulo);
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS_PRODUCTOS.length - 1));

        Row filaSubtitulo = hoja.createRow(1);
        Cell subtituloCell = filaSubtitulo.createCell(0);
        subtituloCell.setCellValue("Generado: " + LocalDateTime.now().format(FMT_FECHA_HORA)
                + "   |   Total de registros: " + productos.size());
        subtituloCell.setCellStyle(estilos.subtitulo);
        hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, HEADERS_PRODUCTOS.length - 1));

        int filaEncabezado = 3;
        Row encabezado = hoja.createRow(filaEncabezado);
        for (int i = 0; i < HEADERS_PRODUCTOS.length; i++) {
            Cell c = encabezado.createCell(i);
            c.setCellValue(HEADERS_PRODUCTOS[i]);
            c.setCellStyle(estilos.encabezadoTabla);
        }

        Drawing<?> drawing = hoja.createDrawingPatriarch();

        int numFila = filaEncabezado + 1;

        for (Producto p : productos) {

            Row fila = hoja.createRow(numFila);
            fila.setHeightInPoints(80);

            CellStyle base = ((numFila - filaEncabezado) % 2 == 0)
                    ? estilos.filaZebra
                    : estilos.filaNormal;

            CellStyle baseNumero = ((numFila - filaEncabezado) % 2 == 0)
                    ? estilos.filaZebraNumero
                    : estilos.filaNormalNumero;

            CellStyle baseFecha = ((numFila - filaEncabezado) % 2 == 0)
                    ? estilos.filaZebraFecha
                    : estilos.filaNormalFecha;

            crearCeldaTexto(fila, 0, String.valueOf(p.getIdProducto()), base);

            insertarImagen(workbook, drawing, hoja, p.getImagen(), numFila, 1);

            crearCeldaTexto(fila, 2, valorOr(p.getFolio(), "-"), base);
            crearCeldaTexto(fila, 3, valorOr(p.getClave(), "-"), base);
            crearCeldaTexto(fila, 4, valorOr(p.getNombre(), "-"), base);
            crearCeldaTexto(fila, 5, valorOr(p.getDescripcion(), "-"), base);

            Departamento depto = p.getDepartamento();
            crearCeldaTexto(fila, 6,
                    depto != null ? valorOr(depto.getNombre(), "-") : "Sin departamento",
                    base);

            Cell precioCell = fila.createCell(7);
            precioCell.setCellValue(p.getPrecio());
            precioCell.setCellStyle(baseNumero);

            Cell statusCell = fila.createCell(8);
            boolean activo = p.getStatus() == 1;
            statusCell.setCellValue(activo ? "Activo" : "Inactivo");
            statusCell.setCellStyle(activo ? estilos.statusActivo : estilos.statusInactivo);

            Usuario usuario = p.getUsuario();

            crearCeldaTexto(fila, 9, usuario != null ? construirNombreUsuario(usuario) : "-", base);
            crearCeldaTexto(fila, 10, usuario != null ? valorOr(usuario.getUsername(), "-") : "-", base);
            crearCeldaTexto(fila, 11, usuario != null ? valorOr(usuario.getEmail(), "-") : "-", base);
            crearCeldaTexto(fila, 12, usuario != null ? valorOr(usuario.getCelular(), "-") : "-", base);
            crearCeldaTexto(fila, 13, usuario != null ? valorOr(usuario.getTelefono(), "-") : "-", base);
            crearCeldaTexto(fila, 14, usuario != null ? obtenerNombreRol(usuario) : "-", base);

            Cell fechaRegCell = fila.createCell(15);
            fechaRegCell.setCellValue(
                    p.getFechaRegistro() != null
                    ? p.getFechaRegistro().format(FMT_FECHA_HORA)
                    : "-"
            );
            fechaRegCell.setCellStyle(baseFecha);

            Cell fechaActCell = fila.createCell(16);
            fechaActCell.setCellValue(
                    p.getFechaActualizacion() != null
                    ? p.getFechaActualizacion().format(FMT_FECHA_HORA)
                    : "-"
            );
            fechaActCell.setCellStyle(baseFecha);

            numFila++;
        }

        if (!productos.isEmpty()) {
            hoja.setAutoFilter(new CellRangeAddress(filaEncabezado, filaEncabezado, 0, HEADERS_PRODUCTOS.length - 1));
        }
        hoja.createFreezePane(0, filaEncabezado + 1);

        for (int i = 0; i < HEADERS_PRODUCTOS.length; i++) {
            hoja.autoSizeColumn(i);
            int anchoActual = hoja.getColumnWidth(i);
            int anchoMaximo = 256 * 45;
            if (anchoActual > anchoMaximo) {
                hoja.setColumnWidth(i, anchoMaximo);
            } else {
                hoja.setColumnWidth(i, anchoActual + 512);
            }
        }
    }

    private void crearCeldaTexto(Row fila, int columna, String valor, CellStyle estilo) {
        Cell c = fila.createCell(columna);
        c.setCellValue(valor);
        c.setCellStyle(estilo);
    }

    private void insertarImagen(XSSFWorkbook workbook,
            Drawing<?> drawing,
            Sheet hoja,
            String base64,
            int fila,
            int columna) {

        if (base64 == null || base64.isBlank()) {
            return;
        }

        try {

            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }

            byte[] bytes = Base64.getDecoder().decode(base64);

            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

            CreationHelper helper = workbook.getCreationHelper();

            ClientAnchor anchor = helper.createClientAnchor();

            // Celda donde inicia la imagen
            anchor.setCol1(columna);
            anchor.setRow1(fila);

            // Celda donde termina (una sola celda)
            anchor.setCol2(columna + 1);
            anchor.setRow2(fila + 1);

            // Márgenes internos (opcional)
            anchor.setDx1(500);
            anchor.setDy1(500);
            anchor.setDx2(-600);
            anchor.setDy2(-800);

            Picture picture = drawing.createPicture(anchor, pictureIdx);

            // NO llamar a resize()
            // picture.resize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String valorOr(String valor, String porDefecto) {
        return (valor == null || valor.isBlank()) ? porDefecto : valor;
    }

    private String construirNombreUsuario(Usuario usuario) {
        StringBuilder sb = new StringBuilder();
        if (usuario.getNombre() != null) {
            sb.append(usuario.getNombre());
        }
        if (usuario.getApellidoPaterno() != null) {
            sb.append(" ").append(usuario.getApellidoPaterno());
        }
        if (usuario.getApellidoMaterno() != null) {
            sb.append(" ").append(usuario.getApellidoMaterno());
        }
        if (sb.length() == 0 && usuario.getUsername() != null) {
            sb.append(usuario.getUsername());
        }
        return sb.length() > 0 ? sb.toString().trim() : "-";
    }

    private String obtenerNombreRol(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return "-";
        }
        try {
            String nombreRol = usuario.getRol().getNombre();
            return valorOr(nombreRol, "-");
        } catch (Exception ex) {
            return "-";
        }
    }

    private void crearHojaEstadisticas(XSSFWorkbook workbook, Estilos estilos, EstadisticasProductos stats) {
        XSSFSheet hoja = workbook.createSheet("Estadísticas");
        int fila = 0;

        fila = escribirTituloSeccion(hoja, estilos, fila, "ESTADÍSTICAS DE PRODUCTOS", 3);
        fila++;

        fila = escribirSubtitulo(hoja, estilos, fila, "Resumen General");
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Total de productos:", String.valueOf(stats.totalProductos));
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Productos activos:",
                stats.activos + " (" + formatoPct(stats.activos, stats.totalProductos) + ")");
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Productos inactivos:",
                stats.inactivos + " (" + formatoPct(stats.inactivos, stats.totalProductos) + ")");
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Departamentos representados:", String.valueOf(stats.totalDepartamentos));
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Usuarios que registraron productos:", String.valueOf(stats.totalUsuariosRegistradores));
        fila++;

        fila = escribirSubtitulo(hoja, estilos, fila, "Precios");
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Precio promedio:", formatoMoneda(stats.precioPromedio));
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Precio mínimo:", formatoMoneda(stats.precioMinimo));
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Precio máximo:", formatoMoneda(stats.precioMaximo));
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Valor total del inventario:", formatoMoneda(stats.valorTotalInventario));
        fila = escribirEtiquetaValor(hoja, estilos, fila, "Valor de inventario activo:", formatoMoneda(stats.valorInventarioActivo));
        if (stats.productoMasCaro != null) {
            fila = escribirEtiquetaValor(hoja, estilos, fila, "Producto más caro:",
                    stats.productoMasCaro + " (" + formatoMoneda(stats.precioMaximo) + ")");
        }
        if (stats.productoMasBarato != null) {
            fila = escribirEtiquetaValor(hoja, estilos, fila, "Producto más económico:",
                    stats.productoMasBarato + " (" + formatoMoneda(stats.precioMinimo) + ")");
        }
        fila++;

        fila = escribirSubtitulo(hoja, estilos, fila, "Productos por Departamento");
        fila = escribirEncabezadoTabla(hoja, estilos, fila, new String[]{"Departamento", "Cantidad", "Valor Total"});
        for (Map.Entry<String, Long> entry : stats.productosPorDepartamento.entrySet()) {
            Row r = hoja.createRow(fila);
            crearCeldaTexto(r, 0, entry.getKey(), estilos.filaNormal);
            Cell cantidadCell = r.createCell(1);
            cantidadCell.setCellValue(entry.getValue());
            cantidadCell.setCellStyle(estilos.filaNormalNumeroEntero);
            Cell valorCell = r.createCell(2);
            valorCell.setCellValue(stats.valorPorDepartamento.getOrDefault(entry.getKey(), 0.0));
            valorCell.setCellStyle(estilos.filaNormalNumero);
            fila++;
        }
        fila++;

        fila = escribirSubtitulo(hoja, estilos, fila, "Detalle de Usuarios Registradores");
        fila = escribirEncabezadoTabla(hoja, estilos, fila,
                new String[]{"Nombre Completo", "Usuario", "Email", "Celular", "Teléfono", "Rol", "Cantidad de Productos", "Valor Total Registrado"});
        for (DetalleUsuario detalle : stats.detallePorUsuario) {
            Usuario u = detalle.usuario;
            Row r = hoja.createRow(fila);
            crearCeldaTexto(r, 0, construirNombreUsuario(u), estilos.filaNormal);
            crearCeldaTexto(r, 1, valorOr(u.getUsername(), "-"), estilos.filaNormal);
            crearCeldaTexto(r, 2, valorOr(u.getEmail(), "-"), estilos.filaNormal);
            crearCeldaTexto(r, 3, valorOr(u.getCelular(), "-"), estilos.filaNormal);
            crearCeldaTexto(r, 4, valorOr(u.getTelefono(), "-"), estilos.filaNormal);
            crearCeldaTexto(r, 5, obtenerNombreRol(u), estilos.filaNormal);
            Cell cantidadCell = r.createCell(6);
            cantidadCell.setCellValue(detalle.cantidad);
            cantidadCell.setCellStyle(estilos.filaNormalNumeroEntero);
            Cell valorCell = r.createCell(7);
            valorCell.setCellValue(detalle.valorTotal);
            valorCell.setCellStyle(estilos.filaNormalNumero);
            fila++;
        }
        fila++;

        if (!stats.topProductosCaros.isEmpty()) {
            fila = escribirSubtitulo(hoja, estilos, fila, "Top " + stats.topProductosCaros.size() + " Productos Más Caros");
            fila = escribirEncabezadoTabla(hoja, estilos, fila, new String[]{"Producto", "Departamento", "Precio"});
            for (Producto p : stats.topProductosCaros) {
                Row r = hoja.createRow(fila);
                crearCeldaTexto(r, 0, valorOr(p.getNombre(), "-"), estilos.filaNormal);
                crearCeldaTexto(r, 1, p.getDepartamento() != null ? valorOr(p.getDepartamento().getNombre(), "-") : "-", estilos.filaNormal);
                Cell precioCell = r.createCell(2);
                precioCell.setCellValue(p.getPrecio());
                precioCell.setCellStyle(estilos.filaNormalNumero);
                fila++;
            }
        }

        hoja.setColumnWidth(0, 256 * 30);
        hoja.setColumnWidth(1, 256 * 20);
        hoja.setColumnWidth(2, 256 * 28);
        hoja.setColumnWidth(3, 256 * 16);
        hoja.setColumnWidth(4, 256 * 16);
        hoja.setColumnWidth(5, 256 * 16);
        hoja.setColumnWidth(6, 256 * 20);
        hoja.setColumnWidth(7, 256 * 20);

    }

    private int escribirTituloSeccion(Sheet hoja, Estilos estilos, int fila, String texto, int columnasAMezclar) {
        Row r = hoja.createRow(fila);
        Cell c = r.createCell(0);
        c.setCellValue(texto);
        c.setCellStyle(estilos.titulo);
        hoja.addMergedRegion(new CellRangeAddress(fila, fila, 0, columnasAMezclar));
        return fila + 1;
    }

    private int escribirSubtitulo(Sheet hoja, Estilos estilos, int fila, String texto) {
        Row r = hoja.createRow(fila);
        Cell c = r.createCell(0);
        c.setCellValue(texto);
        c.setCellStyle(estilos.subtituloSeccion);
        hoja.addMergedRegion(new CellRangeAddress(fila, fila, 0, 2));
        return fila + 1;
    }

    private int escribirEtiquetaValor(Sheet hoja, Estilos estilos, int fila, String etiqueta, String valor) {
        Row r = hoja.createRow(fila);
        Cell etiquetaCell = r.createCell(0);
        etiquetaCell.setCellValue(etiqueta);
        etiquetaCell.setCellStyle(estilos.etiqueta);
        Cell valorCell = r.createCell(1);
        valorCell.setCellValue(valor);
        valorCell.setCellStyle(estilos.valor);
        return fila + 1;
    }

    private int escribirEncabezadoTabla(Sheet hoja, Estilos estilos, int fila, String[] headers) {
        Row r = hoja.createRow(fila);
        for (int i = 0; i < headers.length; i++) {
            Cell c = r.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(estilos.encabezadoTabla);
        }
        return fila + 1;
    }

    private String formatoMoneda(double valor) {
        return String.format(Locale.US, "$%,.2f", valor);
    }

    private String formatoPct(long parte, int total) {
        double pct = total > 0 ? (parte * 100.0 / total) : 0;
        return String.format(Locale.US, "%.1f%%", pct);
    }

    // ================= ESTADÍSTICAS =================
    private EstadisticasProductos calcularEstadisticas(List<Producto> productos) {
        EstadisticasProductos e = new EstadisticasProductos();
        e.totalProductos = productos.size();

        Map<String, Long> porDepartamento = new LinkedHashMap<>();
        Map<String, Double> valorPorDepartamento = new LinkedHashMap<>();
        Map<Long, DetalleUsuario> detallePorUsuario = new LinkedHashMap<>();
        java.util.Set<String> departamentosDistintos = new java.util.HashSet<>();

        double sumaPrecios = 0;
        double sumaPreciosActivos = 0;
        Double precioMin = null;
        Double precioMax = null;
        Producto productoMasCaro = null;
        Producto productoMasBarato = null;
        long activos = 0;

        for (Producto p : productos) {
            double precio = p.getPrecio();
            sumaPrecios += precio;

            boolean activo = p.getStatus() == 1;
            if (activo) {
                activos++;
                sumaPreciosActivos += precio;
            }

            if (precioMin == null || precio < precioMin) {
                precioMin = precio;
                productoMasBarato = p;
            }
            if (precioMax == null || precio > precioMax) {
                precioMax = precio;
                productoMasCaro = p;
            }

            String nombreDepto = (p.getDepartamento() != null && p.getDepartamento().getNombre() != null)
                    ? p.getDepartamento().getNombre() : "Sin departamento";
            porDepartamento.merge(nombreDepto, 1L, Long::sum);
            valorPorDepartamento.merge(nombreDepto, precio, Double::sum);
            departamentosDistintos.add(nombreDepto);

            if (p.getUsuario() != null) {
                Usuario u = p.getUsuario();
                DetalleUsuario detalle = detallePorUsuario.computeIfAbsent(
                        u.getIdUsuario(), id -> new DetalleUsuario(u));
                detalle.cantidad++;
                detalle.valorTotal += precio;
            }
        }

        e.activos = activos;
        e.inactivos = e.totalProductos - activos;
        e.totalDepartamentos = departamentosDistintos.size();
        e.totalUsuariosRegistradores = detallePorUsuario.size();
        e.productosPorDepartamento = porDepartamento;
        e.valorPorDepartamento = valorPorDepartamento;
        e.detallePorUsuario = new ArrayList<>(detallePorUsuario.values());
        e.detallePorUsuario.sort(Comparator.comparingLong((DetalleUsuario d) -> d.cantidad).reversed());
        e.valorTotalInventario = sumaPrecios;
        e.valorInventarioActivo = sumaPreciosActivos;
        e.precioPromedio = e.totalProductos > 0 ? sumaPrecios / e.totalProductos : 0;
        e.precioMinimo = precioMin != null ? precioMin : 0;
        e.precioMaximo = precioMax != null ? precioMax : 0;
        e.productoMasCaro = productoMasCaro != null ? productoMasCaro.getNombre() : null;
        e.productoMasBarato = productoMasBarato != null ? productoMasBarato.getNombre() : null;

        List<Producto> ordenadosPorPrecio = new ArrayList<>(productos);
        ordenadosPorPrecio.sort(Comparator.comparingDouble(Producto::getPrecio).reversed());
        int topN = Math.min(5, ordenadosPorPrecio.size());
        e.topProductosCaros = ordenadosPorPrecio.subList(0, topN);

        return e;
    }

    // ================= PERSISTENCIA EN DISCO =================
    private Path guardarEnDisco(byte[] excelBytes, String nombreArchivo) throws IOException {
        Path carpeta = Paths.get(directorioSalida);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
        Path rutaArchivo = carpeta.resolve(nombreArchivo);
        Files.write(rutaArchivo, excelBytes);
        return rutaArchivo;
    }

    // ================= CLASES AUXILIARES =================
    private static class EstadisticasProductos {

        int totalProductos;
        long activos;
        long inactivos;
        int totalDepartamentos;
        int totalUsuariosRegistradores;
        double valorTotalInventario;
        double valorInventarioActivo;
        double precioPromedio;
        double precioMinimo;
        double precioMaximo;
        String productoMasCaro;
        String productoMasBarato;
        Map<String, Long> productosPorDepartamento;
        Map<String, Double> valorPorDepartamento;
        List<DetalleUsuario> detallePorUsuario;
        List<Producto> topProductosCaros;
    }

    private static class DetalleUsuario {

        final Usuario usuario;
        long cantidad;
        double valorTotal;

        DetalleUsuario(Usuario usuario) {
            this.usuario = usuario;
        }
    }

    private static class Estilos {

        final CellStyle titulo;
        final CellStyle subtitulo;
        final CellStyle subtituloSeccion;
        final CellStyle encabezadoTabla;
        final CellStyle filaNormal;
        final CellStyle filaZebra;
        final CellStyle filaNormalNumero;
        final CellStyle filaZebraNumero;
        final CellStyle filaNormalNumeroEntero;
        final CellStyle filaNormalFecha;
        final CellStyle filaZebraFecha;
        final CellStyle statusActivo;
        final CellStyle statusInactivo;
        final CellStyle etiqueta;
        final CellStyle valor;

        Estilos(XSSFWorkbook workbook) {
            Font fuenteTitulo = workbook.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());

            titulo = crearEstiloBase(workbook);
            titulo.setFont(fuenteTitulo);
            ((XSSFCellStyle) titulo).setFillForegroundColor(new XSSFColor(hexToColor(COLOR_HEADER), null));
            titulo.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            titulo.setAlignment(HorizontalAlignment.LEFT);

            Font fuenteSubtitulo = workbook.createFont();
            fuenteSubtitulo.setItalic(true);
            fuenteSubtitulo.setFontHeightInPoints((short) 10);
            fuenteSubtitulo.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            subtitulo = crearEstiloBase(workbook);
            subtitulo.setFont(fuenteSubtitulo);

            Font fuenteSubtituloSeccion = workbook.createFont();
            fuenteSubtituloSeccion.setBold(true);
            fuenteSubtituloSeccion.setFontHeightInPoints((short) 12);
            subtituloSeccion = crearEstiloBase(workbook);
            subtituloSeccion.setFont(fuenteSubtituloSeccion);
            ((XSSFCellStyle) subtituloSeccion).setFillForegroundColor(new XSSFColor(new Color(217, 226, 243), null));
            subtituloSeccion.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            Font fuenteEncabezado = workbook.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            encabezadoTabla = crearEstiloBase(workbook);
            encabezadoTabla.setFont(fuenteEncabezado);
            ((XSSFCellStyle) encabezadoTabla).setFillForegroundColor(new XSSFColor(hexToColor(COLOR_HEADER), null));
            encabezadoTabla.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            encabezadoTabla.setBorderBottom(BorderStyle.THIN);
            encabezadoTabla.setAlignment(HorizontalAlignment.CENTER);

            filaNormal = crearEstiloBase(workbook);
            filaNormal.setBorderBottom(BorderStyle.HAIR);

            filaZebra = crearEstiloBase(workbook);
            filaZebra.setBorderBottom(BorderStyle.HAIR);
            ((XSSFCellStyle) filaZebra).setFillForegroundColor(new XSSFColor(new Color(247, 249, 251), null));
            filaZebra.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.ss.usermodel.DataFormat formato = workbook.createDataFormat();

            filaNormalNumero = crearEstiloBase(workbook);
            filaNormalNumero.setBorderBottom(BorderStyle.HAIR);
            filaNormalNumero.setDataFormat(formato.getFormat("$#,##0.00"));
            filaNormalNumero.setAlignment(HorizontalAlignment.RIGHT);

            filaZebraNumero = crearEstiloBase(workbook);
            filaZebraNumero.setBorderBottom(BorderStyle.HAIR);
            filaZebraNumero.setDataFormat(formato.getFormat("$#,##0.00"));
            filaZebraNumero.setAlignment(HorizontalAlignment.RIGHT);
            ((XSSFCellStyle) filaZebraNumero).setFillForegroundColor(new XSSFColor(new Color(247, 249, 251), null));
            filaZebraNumero.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            filaNormalNumeroEntero = crearEstiloBase(workbook);
            filaNormalNumeroEntero.setBorderBottom(BorderStyle.HAIR);
            filaNormalNumeroEntero.setDataFormat(formato.getFormat("#,##0"));
            filaNormalNumeroEntero.setAlignment(HorizontalAlignment.RIGHT);

            filaNormalFecha = crearEstiloBase(workbook);
            filaNormalFecha.setBorderBottom(BorderStyle.HAIR);

            filaZebraFecha = crearEstiloBase(workbook);
            filaZebraFecha.setBorderBottom(BorderStyle.HAIR);
            ((XSSFCellStyle) filaZebraFecha).setFillForegroundColor(new XSSFColor(new Color(247, 249, 251), null));
            filaZebraFecha.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            Font fuenteActivo = workbook.createFont();
            fuenteActivo.setBold(true);
            fuenteActivo.setColor(IndexedColors.GREEN.getIndex());
            statusActivo = crearEstiloBase(workbook);
            statusActivo.setFont(fuenteActivo);
            statusActivo.setBorderBottom(BorderStyle.HAIR);
            statusActivo.setAlignment(HorizontalAlignment.CENTER);

            Font fuenteInactivo = workbook.createFont();
            fuenteInactivo.setBold(true);
            fuenteInactivo.setColor(IndexedColors.RED.getIndex());
            statusInactivo = crearEstiloBase(workbook);
            statusInactivo.setFont(fuenteInactivo);
            statusInactivo.setBorderBottom(BorderStyle.HAIR);
            statusInactivo.setAlignment(HorizontalAlignment.CENTER);

            Font fuenteEtiqueta = workbook.createFont();
            fuenteEtiqueta.setBold(true);
            etiqueta = crearEstiloBase(workbook);
            etiqueta.setFont(fuenteEtiqueta);

            valor = crearEstiloBase(workbook);
        }

        private CellStyle crearEstiloBase(XSSFWorkbook workbook) {
            CellStyle estilo = workbook.createCellStyle();
            estilo.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            return estilo;
        }

        private Color hexToColor(String hex) {
            return new Color(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16)
            );
        }
    }
}
