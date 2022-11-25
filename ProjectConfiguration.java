package test;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ProjectConfiguration {

	@Bean
	public Map<String, Node> getAllNodes() {

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Map<String, Node> nodesMap = new HashMap<>();

		try {

			URL url = getClass().getResource("countries.json");
			File file = new File(url.getPath());

			String contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);

			List<Countri> countries = mapper.readValue(contents, new TypeReference<List<Countri>>() {});

			if(!CollectionUtils.isEmpty(countries)) {
				countries.forEach(c -> {

					Node node = new Node();

					if(nodesMap.containsKey(c.getCca3())) {
						node = nodesMap.get(c.getCca3());
					} else {
						node.setCca3(c.getCca3());
					}

					final List<Node> childrens = node.getChildrens();

					if(!CollectionUtils.isEmpty(c.getBorders())) {
						c.getBorders().forEach(b -> {
							if(StringUtils.hasText(b)) {
								Node n = new Node();

								if(nodesMap.containsKey(b)) {
									n = nodesMap.get(b);
								} else {
									n.setCca3(b);
									nodesMap.put(b, n);
								}

								childrens.add(n);
							}
						});
					}

					nodesMap.put(c.getCca3(), node);
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return nodesMap;
	}
}