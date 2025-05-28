package com.yanosik.rcd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
public class StockPrice {
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE)
		@Column(name = "id", nullable = false)
		private Long id;
		private LocalDateTime timestamp;

		@JsonProperty("1. open")
		private BigDecimal open;
		@JsonProperty("2. high")
		private BigDecimal high;
		@JsonProperty("3. low")
		private BigDecimal low;
		@JsonProperty("4. close")
		private BigDecimal close;
		@JsonProperty("5. volume")
		private BigDecimal volume;

		@Override
		public final boolean equals(Object object) {
				if (this == object) return true;
				if (object == null) return false;
				Class<?> oEffectiveClass = object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass() : object.getClass();
				Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
				if (thisEffectiveClass != oEffectiveClass) return false;
				StockPrice that = (StockPrice) object;
				return getId() != null && Objects.equals(getId(), that.getId());
		}

		@Override
		public final int hashCode() {
				return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
		}

		@Override
		public String toString() {
				return "StockPrice{" +
						"id=" + id +
						", timestamp=" + timestamp +
						", open=" + open +
						", high=" + high +
						", low=" + low +
						", close=" + close +
						", volume=" + volume +
						'}';
		}
}
