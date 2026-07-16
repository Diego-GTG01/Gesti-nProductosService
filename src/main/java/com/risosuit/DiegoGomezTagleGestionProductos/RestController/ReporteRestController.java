/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Reporte;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.AuditoriaProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Services.ReportePdfService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private AuditoriaProductoRepository auditoriaRepository;

    @GetMapping("")
    public ResponseEntity<Result<Reporte>> obtenerReportePdf() {
        Result<Reporte> result = new Result();
        result.correct= true;
        
        List<AuditoriaProducto> auditorias = auditoriaRepository.findAll();
        result.object =reportePdfService.generarReporteAuditoriaPdf(auditorias);
        return ResponseEntity.ok().body(result);
    }

}
