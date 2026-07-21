package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Reporte;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.AuditoriaProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.ProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Services.ProductosReporteService;
import com.risosuit.DiegoGomezTagleGestionProductos.Services.ReportePdfService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ALIEN62
 */
@RestController
@RequestMapping("reporte")
public class ReporteRestController {

    @Autowired
    private ReportePdfService reportePdfService;

    @Autowired
    private ProductosReporteService productosReporteService;

    @Autowired
    private AuditoriaProductoRepository auditoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("")
    public ResponseEntity<Result<Reporte>> obtenerReportePdf() {
        Result<Reporte> result = new Result();
        result.correct= true;
        
        List<AuditoriaProducto> auditorias = auditoriaRepository.findAll();
        result.object =reportePdfService.generarReporteAuditoriaPdf(auditorias);
        return ResponseEntity.ok().body(result);
    }

    
    @PostMapping("productos/excel")
    public ResponseEntity<Result<Reporte>> obtenerReporteProductosExcel(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idDepartamento,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax) {

        Result<Reporte> result = new Result();
        result.correct = true;

        List<Producto> productos = productoRepository.findAll().stream()
                .filter(p -> nombre == null
                        || (p.getNombre() != null && p.getNombre().toLowerCase().contains(nombre.toLowerCase())))
                .filter(p -> idDepartamento == null
                        || (p.getDepartamento() != null && p.getDepartamento().getIdDepartamento() == idDepartamento))
                .filter(p -> status == null || p.getStatus() == status)
                .filter(p -> precioMin == null || p.getPrecio() >= precioMin)
                .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
                .collect(java.util.stream.Collectors.toList());

        result.object = productosReporteService.generarReporteProductosExcel(productos);
        return ResponseEntity.ok().body(result);
    }

}