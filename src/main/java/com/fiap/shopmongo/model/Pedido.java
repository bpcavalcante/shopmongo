package com.fiap.shopmongo.model;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("pedido")
public class Pedido {

  @Id
  private String id;
  private String nomeCliente;
  private List<ItemProduto> itensPedido;
  private double valorTotal;

}
