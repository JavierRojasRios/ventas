package org.iesvdm.ventas;

import java.util.Optional;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackages = "org.iesvdm")
@RestController
public class VentasApplication implements CommandLineRunner{

	@Autowired 
	private ClienteDAO clienteDAO;

	@Autowired
	private ComercialDAO comercialDAO;

	public static void main(String[] args) {
		SpringApplication.run(VentasApplication.class, args);
	}

	@GetMapping("/")
	public String home(){
		return "Welcome to the Ventas Application";
	}

	@Override 
	public void run(String... args) throws Exception{
		log.info("*******************************");
		log.info("*Prueba de arranque ClienteDAO*");
		log.info("*******************************");

		clienteDAO.getAll().forEach(c -> log.info("Cliente: {}", c));

		int id = 1;
		Optional<Cliente> cliente = clienteDAO.find(id);

		if(cliente.isPresent()) {
			log.info("Cliente {}: {}", id, cliente.get());

			String nombreOld = cliente.get().getNombre();

			cliente.get().setNombre("Jose M");

			clienteDAO.update(cliente.get());
			log.info("Cliente con id {} actualizado: {} ", id, cliente.get());

			clienteDAO.getAll().forEach(c -> log.info("Cliente: {}", c));

			cliente.get().setNombre(nombreOld);
			clienteDAO.update(cliente.get());
			log.info("Cliente con id {} restaurado: {}", id, cliente.get());

		}else{
			log.info("No se encontró al cliente con id {}", id);
		}

		log.info("*********************************");
		log.info("*Prueba de arranque ComercialDAO*");
		log.info("*********************************");

		comercialDAO.getAll().forEach(c -> log.info("Comercial: {}", c));

		Optional<Comercial> comercial = comercialDAO.find(id);

		if(comercial.isPresent()) {
			log.info("Comercial {}: {}", id, comercial.get());

			String nombreOld = comercial.get().getNombre();

			comercial.get().setNombre("Jose M");

			comercialDAO.update(comercial.get());
			log.info("Comercial con id {} actualizado: {}", id, comercial.get());

			comercialDAO.getAll().forEach(c -> log.info("Comercial: {}", c));

			comercial.get().setNombre(nombreOld);
			comercialDAO.update(comercial.get());
			log.info("Comercial con id {} restaurado: {}", id, comercial.get());

		}else{
			log.info("No se encontró al comercial con id {}", id);
		}

	}

}
