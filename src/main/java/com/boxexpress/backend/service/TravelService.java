package com.boxexpress.backend.service;

import com.boxexpress.backend.model.Travel;
import com.boxexpress.backend.model.TravelStatus;
import com.boxexpress.backend.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRepository travelRepository;

    public List<Travel> getPublicTravels() {
        // Return all travels that are not Cancelled, sorted by departure date
        return travelRepository.findByStatusNot(TravelStatus.CANCELLED, Sort.by(Sort.Direction.ASC, "departureDate"));
    }

    public List<Travel> getAllTravels() {
        return travelRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Travel createTravel(Travel travel) {
        validateDates(travel);
        return travelRepository.save(travel);
    }

    public Travel updateTravel(Long id, Travel travelDetails) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travel not found"));

        validateDates(travelDetails);

        travel.setOrigin(travelDetails.getOrigin());
        travel.setDestination(travelDetails.getDestination());
        travel.setClosingDate(travelDetails.getClosingDate());
        travel.setDepartureDate(travelDetails.getDepartureDate());
        travel.setArrivalDate(travelDetails.getArrivalDate());
        travel.setStatus(travelDetails.getStatus());
        travel.setTransportType(travelDetails.getTransportType());

        return travelRepository.save(travel);
    }

    public void deleteTravel(Long id) {
        travelRepository.deleteById(id);
    }

    private void validateDates(Travel travel) {
        if (travel.getArrivalDate().isBefore(travel.getDepartureDate())) {
            throw new RuntimeException("Arrival date cannot be before Departure date");
        }
        if (travel.getDepartureDate().isBefore(travel.getClosingDate())) {
            throw new RuntimeException("Departure date cannot be before Closing date");
        }
    }
}
