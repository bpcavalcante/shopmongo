package com.fiap.shopmongo.controller;

import com.fiap.shopmongo.model.Pedido;
import com.fiap.shopmongo.service.PedidoService;
import java.util.NoSuchElementException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
@NoArgsConstructor
public class PedidoController {

  @Autowired
  private PedidoService pedidoService;

  @PostMapping
  public ResponseEntity<?> criarPedido(@RequestBody Pedido pedido) {

    try{
      Pedido novoPedido = pedidoService.criarPedido(pedido);
      return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>("Um ou mais produtos não estão disponiveis", HttpStatus.BAD_REQUEST);
    }

  }

}
