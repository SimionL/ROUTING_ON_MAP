package test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing")
public class RouteController {

	@Autowired
	Map<String, Node> allNodes;

	@GetMapping("/{origin}/{destination}")
	public ResponseEntity<Route> getRoute(@PathVariable(value="origin") String origin, @PathVariable(value="destination") String destination) {

		clearNodes();
		Node startNode = allNodes.get(origin);
		Node endNode = allNodes.get(destination);

		Route result = new Route();

		if(startNode != null && endNode != null) {

			List<LinkedList<Node>> allRoutes = new ArrayList<>();

			Queue<Node> queue = new LinkedList<>();
			queue.add(startNode);
			startNode.setVisited(true);

			LinkedList<Node> start = new LinkedList<>();
			start.add(startNode);
			allRoutes.add(start);

			Set<Node> childrens = new LinkedHashSet<>();

			boolean isRouteIdentified = false;
			do {
				Node n = (Node)queue.remove();

				childrens.addAll(n.getChildrens());

				if(!isRouteIdentified) {
					isRouteIdentified = destination.equals(n.getCca3());
				}

				List<LinkedList<Node>> newLists = new ArrayList<>();

				allRoutes.forEach(r -> {
					if(!CollectionUtils.isEmpty(r)) {
						Node lastElement = r.get(r.size() - 1);
						childrens.forEach(c -> {
							if(c != null && countriesAreNeighbors(lastElement, c) && !c.isVisited()) {
								LinkedList<Node> newList = new LinkedList<>(r);
								newList.add(c);
								newLists.add(newList);

							}
						});
					}
				});

				childrens.forEach(v -> v.setVisited(true));

				if(!CollectionUtils.isEmpty(newLists)) {
					allRoutes.addAll(newLists);
				}

				if(queue.isEmpty()) {
					if(isRouteIdentified && !CollectionUtils.isEmpty(allRoutes)) {
						break;
					}
					childrens.forEach(c -> {
						queue.add(c);
					});
					childrens.clear();
				}
			}

			while(!queue.isEmpty());

			final LinkedList<Node> route = new LinkedList<>();

			allRoutes.forEach(r -> {
				if(!CollectionUtils.isEmpty(r)) {
					Node lastElement = r.get(r.size() - 1);
					if(lastElement != null && 
							destination.equals(lastElement.getCca3()) &&
							(CollectionUtils.isEmpty(route) || route.size() > r.size())) {

						route.clear();
						route.addAll(r);
					}
				}
			});

			final LinkedList<String> stringRoute = new LinkedList<>();

			route.forEach(r -> {
				if(r != null && StringUtils.hasText(r.getCca3())) {
					stringRoute.add(r.getCca3());
				}
			});

			result.setRoute(stringRoute);

			clearNodes();
		}

		if(CollectionUtils.isEmpty(result.getRoute())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
	}

	private boolean countriesAreNeighbors(final Node n_1, Node n_2) {

		boolean areNeighbors_1 = n_2.getChildrens().stream().filter(c -> c.getCca3().equals(n_1.getCca3())).findAny().isPresent();
		boolean areNeighbors_2 = n_1.getChildrens().stream().filter(c -> c.getCca3().equals(n_2.getCca3())).findAny().isPresent();

		return areNeighbors_1 || areNeighbors_2;
	}

	private void clearNodes() {
		for(Node nod : allNodes.values()) {
			nod.setVisited(false);
		}
	}
}