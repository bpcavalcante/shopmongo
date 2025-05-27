package com.fiap.shopmongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.shopmongo.model.ItemProduto;
import com.fiap.shopmongo.model.Pedido;
import com.fiap.shopmongo.repository.PedidoRepository;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PedidoService {

  @Autowired private PedidoRepository pedidoRepository;

  @Autowired private RestTemplate restTemplate;

  @Autowired private ObjectMapper objectMapper;

  public Pedido criarPedido(Pedido pedido) {

    boolean produtoDisponiveis = verificarDisponibilidadeProduto(pedido.getItensPedido());

    if (!produtoDisponiveis) {
      throw new NoSuchElementException("Um ou mais produtos não estão disponiveis");
    }

    double valorTotal = calcularValorTotal(pedido.getItensPedido());
    pedido.setValorTotal(valorTotal);

    atualizarEstoqueProdutos(pedido.getItensPedido());

    return pedidoRepository.save(pedido);
  }

  private boolean verificarDisponibilidadeProduto(List<ItemProduto> itensPedido) {
    for (ItemProduto item : itensPedido) {

      Integer idProduto = item.getIdProduto();
      int quantidade = item.getQuantidade();

      ResponseEntity<String> response =
          restTemplate.getForEntity(
              "http://localhost:8080/produtos/{produtoId}", String.class, idProduto);

      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NoSuchElementException("Produto não encontrado");
      } else {
        try {
          JsonNode produtoJson = objectMapper.readTree(response.getBody());
          int quantidadeEstoque = produtoJson.get("quantidade_estoque").asInt();
          if (quantidadeEstoque < quantidade) {
            return false;
          }
        } catch (IOException e) {

        }
      }
    }
    return true;
  }

  private double calcularValorTotal(List<ItemProduto> itensPedido) {

    double valorTotal = 0.0;

    for (ItemProduto item : itensPedido) {
      Integer idProduto = item.getIdProduto();
      int quantidade = item.getQuantidade();

      ResponseEntity<String> response =
          restTemplate.getForEntity(
              "http://localhost:8080/produtos/{produtoId}", String.class, idProduto);

      if (response.getStatusCode() == HttpStatus.OK) {

        try {
          JsonNode produtoJson = objectMapper.readTree(response.getBody());
          double preco = produtoJson.get("preco").asDouble();
          valorTotal += preco * quantidade;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return valorTotal;
  }

  private void atualizarEstoqueProdutos(List<ItemProduto> itensPedido) {
    for (ItemProduto item : itensPedido) {
      Integer idProduto = item.getIdProduto();
      int quantidade = item.getQuantidade();

      restTemplate.put(
          "http://localhost:8080/produtos/atualizar/estoque/{id}/{quantidade}",
          null,
          idProduto,
          quantidade);
    }
  }
}
