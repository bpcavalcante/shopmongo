package com.fiap.shopmongo.repository;

import com.fiap.shopmongo.model.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PedidoRepository extends MongoRepository<Pedido, String> {

}
