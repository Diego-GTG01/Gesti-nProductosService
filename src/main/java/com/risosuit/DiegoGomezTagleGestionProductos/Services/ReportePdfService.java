package com.risosuit.DiegoGomezTagleGestionProductos.Services;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Reporte;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Genera el reporte de auditoría de productos en formato PDF.
 * - Devuelve el archivo codificado en Base64 dentro de un objeto Reporte.
 * - Además guarda una copia física del PDF en una carpeta configurable.
 *
 * Requiere la dependencia de Apache PDFBox en el pom.xml:
 *
 * <dependency>
 *     <groupId>org.apache.pdfbox</groupId>
 *     <artifactId>pdfbox</artifactId>
 *     <version>2.0.30</version>
 * </dependency>
 */
@Service
public class ReportePdfService {

    // Carpeta donde se guarda el PDF físico. Configurable en application.properties:
    // reportes.pdf.directorio=C:/reportes/auditoria  (o una ruta relativa/absoluta que prefieras)
    @Value("${reportes.pdf.directorio:reportes/auditoria}")
    private String directorioSalida;

    private static final float MARGEN = 40f;
    private static final float ANCHO_PAGINA = PDRectangle.A4.getWidth();
    private static final float ALTO_PAGINA = PDRectangle.A4.getHeight();
    private static final float ALTO_LINEA = 14f;
    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] HEADERS = {"ID", "Fecha y Hora", "Usuario", "Producto", "Operación", "Descripción"};
    private static final float[] ANCHOS_COL = {35f, 80f, 85f, 85f, 65f, 130f};

    private final PDFont fuenteTitulo = PDType1Font.HELVETICA_BOLD;
    private final PDFont fuenteNegrita = PDType1Font.HELVETICA_BOLD;
    private final PDFont fuenteNormal = PDType1Font.HELVETICA;

    public Reporte generarReporteAuditoriaPdf(List<AuditoriaProducto> auditorias) {
        Reporte reporte = new Reporte();

        try (PDDocument documento = new PDDocument()) {

            Estadisticas stats = calcularEstadisticas(auditorias);

            EstadoPdf estado = new EstadoPdf(documento);
            estado.abrirNuevaPagina();

            escribirTitulo(estado);
            escribirMetadatos(estado, auditorias.size());
            escribirEstadisticas(estado, stats);
            escribirEncabezadoTabla(estado);

            for (AuditoriaProducto auditoria : auditorias) {
                escribirFilaAuditoria(estado, auditoria);
            }

            estado.cerrar();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            documento.save(bos);
            byte[] pdfBytes = bos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pdfBytes);

            String nombreArchivo = "reporte_auditoria_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".pdf";
            Path ruta = guardarEnDisco(pdfBytes, nombreArchivo);

            reporte.setStatus(1);
            reporte.setMessage("Reporte de auditoría generado y guardado en: " + ruta.toAbsolutePath());
            reporte.setNombre("Reporte de Auditoría de Productos");
            reporte.setFileName(nombreArchivo);
            reporte.setFile(base64);

        } catch (IOException e) {
            reporte.setStatus(0);
            reporte.setMessage("Error al generar el reporte PDF de auditoría: " + e.getMessage());
        }

        return reporte;
    }

    // ================= ESTADÍSTICAS =================

    private Estadisticas calcularEstadisticas(List<AuditoriaProducto> auditorias) {
        Estadisticas e = new Estadisticas();
        e.totalMovimientos = auditorias.size();

        Map<String, Long> porOperacion = new LinkedHashMap<>();
        Map<String, Long> porUsuario = new LinkedHashMap<>();
        Map<Long, Long> conteoPorProducto = new LinkedHashMap<>();
        Map<Long, String> nombrePorProducto = new LinkedHashMap<>();
        java.util.Set<Long> productosDistintos = new java.util.HashSet<>();
        java.util.Set<Long> usuariosDistintos = new java.util.HashSet<>();
        Map<LocalDate, Long> conteoPorDia = new java.util.HashMap<>();
        Map<YearMonth, Long> conteoPorMes = new TreeMap<>();
        LocalDateTime minFecha = null;
        LocalDateTime maxFecha = null;

        for (AuditoriaProducto a : auditorias) {
            String operacion = a.getTipoOperacion() != null ? a.getTipoOperacion().getNombre() : "Sin especificar";
            porOperacion.merge(operacion, 1L, Long::sum);

            String usuario = a.getUsuario() != null ? a.getUsuario().getUsername() : "Sistema / Anónimo";
            porUsuario.merge(usuario, 1L, Long::sum);
            if (a.getUsuario() != null) {
                usuariosDistintos.add(a.getUsuario().getIdUsuario());
            }

            if (a.getProducto() != null) {
                long idProd = a.getProducto().getIdProducto();
                productosDistintos.add(idProd);
                conteoPorProducto.merge(idProd, 1L, Long::sum);
                nombrePorProducto.putIfAbsent(idProd, a.getProducto().getNombre());
            }

            LocalDateTime fecha = a.getFechaOperacion();
            if (fecha != null) {
                if (minFecha == null || fecha.isBefore(minFecha)) minFecha = fecha;
                if (maxFecha == null || fecha.isAfter(maxFecha)) maxFecha = fecha;
                conteoPorDia.merge(fecha.toLocalDate(), 1L, Long::sum);
                conteoPorMes.merge(YearMonth.from(fecha), 1L, Long::sum);
            }
        }

        e.movimientosPorOperacion = porOperacion;
        e.movimientosPorUsuario = porUsuario;
        e.productosDistintos = productosDistintos.size();
        e.usuariosDistintos = usuariosDistintos.size();
        e.fechaPrimeraAuditoria = minFecha;
        e.fechaUltimaAuditoria = maxFecha;

        if (minFecha != null && maxFecha != null) {
            e.diasCubiertos = ChronoUnit.DAYS.between(minFecha.toLocalDate(), maxFecha.toLocalDate()) + 1;
        }

        if (!conteoPorDia.isEmpty()) {
            e.promedioMovimientosPorDiaActivo = e.totalMovimientos / (double) conteoPorDia.size();
            Map.Entry<LocalDate, Long> diaTop = null;
            for (Map.Entry<LocalDate, Long> ent : conteoPorDia.entrySet()) {
                if (diaTop == null || ent.getValue() > diaTop.getValue()) diaTop = ent;
            }
            e.diaConMasMovimientos = diaTop.getKey();
            e.cantidadDiaConMasMovimientos = diaTop.getValue();
        }

        Map<String, Long> porMesFormateado = new LinkedHashMap<>();
        DateTimeFormatter fmtMes = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));
        for (Map.Entry<YearMonth, Long> ent : conteoPorMes.entrySet()) {
            porMesFormateado.put(capitalizar(ent.getKey().format(fmtMes)), ent.getValue());
        }
        e.movimientosPorMes = porMesFormateado;

        List<Map.Entry<Long, Long>> productosOrdenados = new ArrayList<>(conteoPorProducto.entrySet());
        productosOrdenados.sort((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()));
        Map<String, Long> topProductos = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(5, productosOrdenados.size()); i++) {
            Map.Entry<Long, Long> ent = productosOrdenados.get(i);
            topProductos.put(nombrePorProducto.getOrDefault(ent.getKey(), "Producto #" + ent.getKey()), ent.getValue());
        }
        e.topProductos = topProductos;

        if (!productosOrdenados.isEmpty()) {
            Map.Entry<Long, Long> top = productosOrdenados.get(0);
            e.productoMasAuditado = nombrePorProducto.getOrDefault(top.getKey(), "Producto #" + top.getKey());
            e.cantidadProductoMasAuditado = top.getValue();
        }

        for (Map.Entry<String, Long> ent : porUsuario.entrySet()) {
            if (e.usuarioMasActivo == null || ent.getValue() > e.cantidadUsuarioMasActivo) {
                e.usuarioMasActivo = ent.getKey();
                e.cantidadUsuarioMasActivo = ent.getValue();
            }
        }

        return e;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private void escribirTitulo(EstadoPdf estado) throws IOException {
        estado.asegurarEspacio(30f);
        estado.cs.beginText();
        estado.cs.setFont(fuenteTitulo, 16);
        estado.cs.newLineAtOffset(MARGEN, estado.y);
        estado.cs.showText("REPORTE DE AUDITORÍA DE PRODUCTOS");
        estado.cs.endText();
        estado.y -= 26f;
    }

    private void escribirMetadatos(EstadoPdf estado, int totalRegistros) throws IOException {
        String fechaActual = LocalDateTime.now().format(FMT_FECHA_HORA);
        escribirLineaEtiquetaValor(estado, "Fecha de Generación:", fechaActual);
        escribirLineaEtiquetaValor(estado, "Responsable:", "Sistema de Inventario");
        escribirLineaEtiquetaValor(estado, "Total Movimientos:", String.valueOf(totalRegistros));
        estado.y -= 6f;
    }

    private void escribirSubtitulo(EstadoPdf estado, String texto) throws IOException {
        texto = sanitizarTexto(texto);
        estado.asegurarEspacio(ALTO_LINEA + 8f);
        estado.y -= 4f;
        estado.cs.beginText();
        estado.cs.setFont(fuenteNegrita, 12);
        estado.cs.newLineAtOffset(MARGEN, estado.y);
        estado.cs.showText(texto);
        estado.cs.endText();
        estado.y -= ALTO_LINEA + 2;
    }

    private void escribirEstadisticas(EstadoPdf estado, Estadisticas stats) throws IOException {
        escribirSubtitulo(estado, "Estadísticas de la Auditoría");

        escribirSubtitulo(estado, "Resumen General");
        escribirLineaEtiquetaValor(estado, "Total de movimientos:", String.valueOf(stats.totalMovimientos));
        escribirLineaEtiquetaValor(estado, "Productos distintos afectados:", String.valueOf(stats.productosDistintos));
        escribirLineaEtiquetaValor(estado, "Usuarios distintos que realizaron cambios:", String.valueOf(stats.usuariosDistintos));
        if (stats.fechaPrimeraAuditoria != null && stats.fechaUltimaAuditoria != null) {
            escribirLineaEtiquetaValor(estado, "Primer movimiento registrado:", stats.fechaPrimeraAuditoria.format(FMT_FECHA_HORA));
            escribirLineaEtiquetaValor(estado, "Último movimiento registrado:", stats.fechaUltimaAuditoria.format(FMT_FECHA_HORA));
            escribirLineaEtiquetaValor(estado, "Periodo cubierto:", stats.diasCubiertos + " día(s)");
        }

        escribirSubtitulo(estado, "Actividad");
        if (stats.diaConMasMovimientos != null) {
            escribirLineaEtiquetaValor(estado, "Promedio de movimientos por día activo:",
                    String.format(Locale.US, "%.2f", stats.promedioMovimientosPorDiaActivo));
            escribirLineaEtiquetaValor(estado, "Día con mayor actividad:",
                    stats.diaConMasMovimientos.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            + " (" + stats.cantidadDiaConMasMovimientos + " movimiento(s))");
        }
        if (stats.usuarioMasActivo != null) {
            escribirLineaEtiquetaValor(estado, "Usuario más activo:",
                    stats.usuarioMasActivo + " (" + stats.cantidadUsuarioMasActivo + " movimiento(s))");
        }
        if (stats.productoMasAuditado != null) {
            escribirLineaEtiquetaValor(estado, "Producto más auditado:",
                    stats.productoMasAuditado + " (" + stats.cantidadProductoMasAuditado + " movimiento(s))");
        }

        escribirSubtitulo(estado, "Movimientos por Tipo de Operación");
        for (Map.Entry<String, Long> entry : stats.movimientosPorOperacion.entrySet()) {
            double pct = stats.totalMovimientos > 0 ? (entry.getValue() * 100.0 / stats.totalMovimientos) : 0;
            escribirLineaEtiquetaValor(estado, "   • " + entry.getKey() + ":",
                    entry.getValue() + " (" + String.format(Locale.US, "%.1f", pct) + "%)");
        }

        escribirSubtitulo(estado, "Movimientos por Usuario");
        for (Map.Entry<String, Long> entry : stats.movimientosPorUsuario.entrySet()) {
            double pct = stats.totalMovimientos > 0 ? (entry.getValue() * 100.0 / stats.totalMovimientos) : 0;
            escribirLineaEtiquetaValor(estado, "   • " + entry.getKey() + ":",
                    entry.getValue() + " (" + String.format(Locale.US, "%.1f", pct) + "%)");
        }

        if (stats.topProductos != null && !stats.topProductos.isEmpty()) {
            escribirSubtitulo(estado, "Top " + stats.topProductos.size() + " Productos con Más Movimientos");
            int puesto = 1;
            for (Map.Entry<String, Long> entry : stats.topProductos.entrySet()) {
                escribirLineaEtiquetaValor(estado, "   " + puesto + ". " + entry.getKey() + ":", String.valueOf(entry.getValue()));
                puesto++;
            }
        }

        if (stats.movimientosPorMes != null && !stats.movimientosPorMes.isEmpty()) {
            escribirSubtitulo(estado, "Movimientos por Mes");
            for (Map.Entry<String, Long> entry : stats.movimientosPorMes.entrySet()) {
                double pct = stats.totalMovimientos > 0 ? (entry.getValue() * 100.0 / stats.totalMovimientos) : 0;
                escribirLineaEtiquetaValor(estado, "   • " + entry.getKey() + ":",
                        entry.getValue() + " (" + String.format(Locale.US, "%.1f", pct) + "%)");
            }
        }

        estado.y -= 10f;
    }

    private void escribirLineaEtiquetaValor(EstadoPdf estado, String etiqueta, String valor) throws IOException {
        etiqueta = sanitizarTexto(etiqueta);
        valor = sanitizarTexto(valor);
        estado.asegurarEspacio(ALTO_LINEA);
        estado.cs.beginText();
        estado.cs.setFont(fuenteNegrita, 10);
        estado.cs.newLineAtOffset(MARGEN, estado.y);
        estado.cs.showText(etiqueta);
        estado.cs.endText();

        if (valor != null && !valor.isEmpty()) {
            float anchoEtiqueta = fuenteNegrita.getStringWidth(etiqueta) / 1000f * 10f;
            estado.cs.beginText();
            estado.cs.setFont(fuenteNormal, 10);
            estado.cs.newLineAtOffset(MARGEN + anchoEtiqueta + 6f, estado.y);
            estado.cs.showText(valor);
            estado.cs.endText();
        }
        estado.y -= ALTO_LINEA;
    }

    private void escribirEncabezadoTabla(EstadoPdf estado) throws IOException {
        estado.asegurarEspacio(ALTO_LINEA + 8f);
        float x = MARGEN;
        float alturaFila = ALTO_LINEA + 6f;

        estado.cs.setNonStrokingColor(27, 54, 93); // azul corporativo #1B365D
        estado.cs.addRect(MARGEN, estado.y - alturaFila + 4f, ANCHO_PAGINA - 2 * MARGEN, alturaFila);
        estado.cs.fill();
        estado.cs.setNonStrokingColor(0, 0, 0);

        estado.cs.beginText();
        estado.cs.setFont(fuenteNegrita, 9);
        estado.cs.setNonStrokingColor(255, 255, 255);
        estado.cs.newLineAtOffset(x + 3f, estado.y - alturaFila + 8f);
        for (int i = 0; i < HEADERS.length; i++) {
            estado.cs.showText(HEADERS[i]);
            if (i < HEADERS.length - 1) {
                estado.cs.newLineAtOffset(ANCHOS_COL[i], 0);
            }
        }
        estado.cs.endText();
        estado.cs.setNonStrokingColor(0, 0, 0);

        estado.y -= alturaFila;
        estado.filaZebra = false;
        estado.paginaNueva = false;
    }

    private void escribirFilaAuditoria(EstadoPdf estado, AuditoriaProducto auditoria) throws IOException {
        String idStr = String.valueOf(auditoria.getIdAuditoria());
        String fechaStr = auditoria.getFechaOperacion() != null ? auditoria.getFechaOperacion().format(FMT_FECHA_HORA) : "-";
        String usuarioStr = auditoria.getUsuario() != null ? auditoria.getUsuario().getUsername() : "Sistema / Anónimo";
        String productoStr = auditoria.getProducto() != null ? auditoria.getProducto().getNombre() : "N/A";
        String operacionStr = auditoria.getTipoOperacion() != null ? auditoria.getTipoOperacion().getNombre() : "-";
        String descripcionStr = auditoria.getDescripcionCambio() != null ? auditoria.getDescripcionCambio() : "";

        List<String> lineasDescripcion = dividirTexto(descripcionStr, fuenteNormal, 8, ANCHOS_COL[5] - 6f);
        int numLineas = Math.max(1, lineasDescripcion.size());
        float alturaFila = numLineas * ALTO_LINEA;

        estado.asegurarEspacio(alturaFila);
        if (estado.paginaNueva) {
            escribirEncabezadoTabla(estado);
            estado.paginaNueva = false;
        }

        if (estado.filaZebra) {
            estado.cs.setNonStrokingColor(247, 249, 251); // gris zebra
            estado.cs.addRect(MARGEN, estado.y - alturaFila + 4f, ANCHO_PAGINA - 2 * MARGEN, alturaFila);
            estado.cs.fill();
            estado.cs.setNonStrokingColor(0, 0, 0);
        }
        estado.filaZebra = !estado.filaZebra;

        float x = MARGEN + 3f;
        float yTexto = estado.y - ALTO_LINEA + 8f;

        estado.cs.beginText();
        estado.cs.setFont(fuenteNormal, 8);
        estado.cs.newLineAtOffset(x, yTexto);
        estado.cs.showText(idStr);
        estado.cs.newLineAtOffset(ANCHOS_COL[0], 0);
        estado.cs.showText(fechaStr);
        estado.cs.newLineAtOffset(ANCHOS_COL[1], 0);
        estado.cs.showText(recortar(usuarioStr, fuenteNormal, 8, ANCHOS_COL[2] - 6f));
        estado.cs.newLineAtOffset(ANCHOS_COL[2], 0);
        estado.cs.showText(recortar(productoStr, fuenteNormal, 8, ANCHOS_COL[3] - 6f));
        estado.cs.newLineAtOffset(ANCHOS_COL[3], 0);
        estado.cs.showText(recortar(operacionStr, fuenteNormal, 8, ANCHOS_COL[4] - 6f));
        estado.cs.endText();

        float xDescripcion = MARGEN;
        for (int i = 0; i < 5; i++) xDescripcion += ANCHOS_COL[i];
        float yDescripcion = estado.y - ALTO_LINEA + 8f;
        for (String linea : lineasDescripcion) {
            estado.cs.beginText();
            estado.cs.setFont(fuenteNormal, 8);
            estado.cs.newLineAtOffset(xDescripcion + 3f, yDescripcion);
            estado.cs.showText(linea);
            estado.cs.endText();
            yDescripcion -= ALTO_LINEA;
        }

        estado.y -= alturaFila;
    }

    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        String limpio = texto.replaceAll("[\\n\\r\\t]+", " ").trim();
        StringBuilder sb = new StringBuilder(limpio.length());
        java.nio.charset.CharsetEncoder encoder = java.nio.charset.Charset.forName("Cp1252").newEncoder();
        for (int i = 0; i < limpio.length(); i++) {
            char c = limpio.charAt(i);
            if (c < 0x20) {
                sb.append(' ');
            } else if (encoder.canEncode(c)) {
                sb.append(c);
            } else {
                sb.append('?');
            }
        }
        return sb.toString();
    }

    private String recortar(String texto, PDFont fuente, float tamano, float anchoMaximo) throws IOException {
        if (texto == null) return "";
        texto = sanitizarTexto(texto);
        if (fuente.getStringWidth(texto) / 1000f * tamano <= anchoMaximo) return texto;
        String resultado = texto;
        while (resultado.length() > 1 && fuente.getStringWidth(resultado + "...") / 1000f * tamano > anchoMaximo) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        return resultado + "...";
    }

    private List<String> dividirTexto(String texto, PDFont fuente, float tamano, float anchoMaximo) throws IOException {
        List<String> lineas = new ArrayList<>();
        texto = sanitizarTexto(texto);
        if (texto == null || texto.isEmpty()) {
            lineas.add("");
            return lineas;
        }
        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();

        for (String palabra : palabras) {
            String candidato = lineaActual.length() == 0 ? palabra : lineaActual + " " + palabra;
            float ancho = fuente.getStringWidth(candidato) / 1000f * tamano;
            if (ancho > anchoMaximo && lineaActual.length() > 0) {
                lineas.add(lineaActual.toString());
                lineaActual = new StringBuilder(palabra);
            } else {
                lineaActual = new StringBuilder(candidato);
            }
        }
        if (lineaActual.length() > 0) {
            lineas.add(lineaActual.toString());
        }
        if (lineas.isEmpty()) lineas.add("");
        return lineas;
    }

    // ================= PERSISTENCIA EN DISCO =================

    private Path guardarEnDisco(byte[] pdfBytes, String nombreArchivo) throws IOException {
        Path carpeta = Paths.get(directorioSalida);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
        Path rutaArchivo = carpeta.resolve(nombreArchivo);
        Files.write(rutaArchivo, pdfBytes);
        return rutaArchivo;
    }

    private static class Estadisticas {
        int totalMovimientos;
        int productosDistintos;
        int usuariosDistintos;
        LocalDateTime fechaPrimeraAuditoria;
        LocalDateTime fechaUltimaAuditoria;
        long diasCubiertos;
        double promedioMovimientosPorDiaActivo;
        LocalDate diaConMasMovimientos;
        long cantidadDiaConMasMovimientos;
        String usuarioMasActivo;
        long cantidadUsuarioMasActivo;
        String productoMasAuditado;
        long cantidadProductoMasAuditado;
        Map<String, Long> movimientosPorOperacion;
        Map<String, Long> movimientosPorUsuario;
        Map<String, Long> movimientosPorMes;
        Map<String, Long> topProductos;
    }

    private class EstadoPdf {
        final PDDocument documento;
        PDPage pagina;
        PDPageContentStream cs;
        float y;
        boolean filaZebra = false;
        boolean paginaNueva = false;

        EstadoPdf(PDDocument documento) {
            this.documento = documento;
        }

        void abrirNuevaPagina() throws IOException {
            pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            cs = new PDPageContentStream(documento, pagina);
            y = ALTO_PAGINA - MARGEN;
        }

        void asegurarEspacio(float alturaNecesaria) throws IOException {
            if (y - alturaNecesaria < MARGEN) {
                cs.close();
                abrirNuevaPagina();
                paginaNueva = true;
            }
        }

        void cerrar() throws IOException {
            cs.close();
        }
    }
}