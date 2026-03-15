package com.birgundegelecek.proje.status;

import org.hibernate.query.criteria.CriteriaDefinition;

public enum SiparisStatus {
	IADE_EDILDI,
	IADE_KABUL_EDILDI,
	IADE_BEKLENIYOR,
	TAMAMLANDI,
	KARGODA,
	HAZIRLANIYOR,
	SIPARIS_ALINDI,
	ODEME_ALINDI,
}
