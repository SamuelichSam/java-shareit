package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.user.id = :userId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    List<Booking> findByUserIdOrderByStartDesc(Long userId);

    List<Booking> findByUserIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, State state);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, Status status);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndUserIdAndStatusAndEndBefore(Long itemId, Long bookerId, Status status,
                                                             LocalDateTime endBefore);
}
