package com.osbah.thycase.domain.route.service;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.route.model.Route;
import com.osbah.thycase.domain.route.usecase.RouteUseCase;
import com.osbah.thycase.domain.transportation.model.Transportation;
import com.osbah.thycase.domain.transportation.port.TransportationPort;
import com.osbah.thycase.shared.exception.ExceptionCode;
import com.osbah.thycase.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class RouteFinderService implements RouteUseCase {

    private static final Map<Long, List<Transportation>> flightsByOrigin = new HashMap<>();
    private static final Map<Long, List<Transportation>> nonFlightsByOrigin = new HashMap<>();
    private final TransportationPort transportationPort;

    private static List<Route> mapToRouteList(List<List<Transportation>> results) {
        return results.stream()
                .map(route -> {
                    Transportation first = route.getFirst();
                    Transportation last = route.getLast();

                    Location origin = first.getOriginLocation();
                    Location destination = last.getDestinationLocation();

                    return new Route(origin, destination, List.copyOf(route));
                })
                .toList();
    }

    //If current node is destination and route must contains one flight rule provided it is a valid route
    private static void addToResultIfValidRoute(long node, long destinationId, int flightsUsed, Deque<Transportation> path, List<List<Transportation>> results) {
        if (node == destinationId && flightsUsed == 1 && !path.isEmpty()) {
            results.add(new ArrayList<>(path));
        }
    }

    @Override
    public List<Route> findRoutesDFS(long originId, long destinationId, Integer operatingDay) {

        validateRouteSearch(originId, destinationId);

        //Contains valid routes
        List<List<Transportation>> results = new ArrayList<>();
        //Active DFS Route
        Deque<Transportation> path = new ArrayDeque<>(3);
        //Contains visited nodes to prevent loop
        Set<String> visited = new HashSet<>();

        dfs(originId, destinationId, operatingDay, 0, 0, 0, 3, path, results, visited);

        //Clear static maps to prevent memory leak
        flightsByOrigin.clear();
        nonFlightsByOrigin.clear();

        return mapToRouteList(results);
    }

    private static void validateRouteSearch(long originId, long destinationId) {
        if (originId == destinationId) {
            log.error("OriginLocationId and DestinationLocationId must be different");
            throw new ValidationException(ExceptionCode.TRANSPORTATION_ORIGIN_AND_DESTINATION_CANNOT_BE_SAME.getErrorCode(), ExceptionCode.ORIGIN_AND_DESTINATION_MUST_BE_DIFFERENT.formatMessage());
        }
    }

    private void dfs(long node,
                     long destinationId,
                     Integer dayOfWeek,
                     int flightsUsed,
                     int nonFlightBefore,
                     int nonFlightAfter,
                     int stepsLeft,
                     Deque<Transportation> path,
                     List<List<Transportation>> results,
                     Set<String> visited) {

        addToResultIfValidRoute(node, destinationId, flightsUsed, path, results);

        //Max step limit check
        if (stepsLeft == 0) return;

        //Create unique key for loop prevention
        String key = node + "|" + flightsUsed + "|" + nonFlightBefore + "|" + nonFlightAfter + "|" + stepsLeft;
        //If key already visited stop
        if (!visited.add(key)) return;

        if (flightsUsed == 0) {
            //If flight not used, search for before-flight transportation or flight
            if (nonFlightBefore == 0) {
                //If before-flight not found already, search for before-flight
                searchNode(getNonFlightTransportations(node, dayOfWeek), path, destinationId, dayOfWeek, flightsUsed, nonFlightBefore + 1, nonFlightAfter, stepsLeft, results, visited);
            }
            // Search for flight transportation
            searchNode(getFlightTransportations(node, dayOfWeek), path, destinationId, dayOfWeek, 1, nonFlightBefore, nonFlightAfter, stepsLeft, results, visited);
        } else {
            // If flight already found, search for after-flight transportation
            if (nonFlightAfter == 0) {
                searchNode(getNonFlightTransportations(node, dayOfWeek), path, destinationId, dayOfWeek, flightsUsed, nonFlightBefore, nonFlightAfter + 1, stepsLeft, results, visited);
            }
        }
        //It is unlocked so that it can be visitable again by another branch.
        visited.remove(key);
    }

    private void searchNode(List<Transportation> node, Deque<Transportation> path, long destinationId, Integer dayOfWeek, int flightsUsed, int nonFlightBefore, int nonFlightAfter, int stepsLeft, List<List<Transportation>> results, Set<String> visited) {
        for (Transportation nonFlightEdge : node) {
            path.addLast(nonFlightEdge);
            dfs(nonFlightEdge.getDestinationLocation().getId(), destinationId, dayOfWeek,
                    flightsUsed, nonFlightBefore, nonFlightAfter,
                    stepsLeft - 1, path, results, visited);
            path.removeLast();
        }
    }

    private List<Transportation> getFlightTransportations(long originId, Integer operatingDay) {
        return flightsByOrigin.computeIfAbsent(originId, id -> transportationPort.findFlightTransportationsByOrigin(id, operatingDay));
    }

    private List<Transportation> getNonFlightTransportations(long originId, Integer operatingDay) {
        return nonFlightsByOrigin.computeIfAbsent(originId, id -> transportationPort.findNonFlightTransportationsByOrigin(id, operatingDay));
    }
}
