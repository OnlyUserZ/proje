package com.birgundegelecek.proje.status;

import org.hibernate.query.criteria.CriteriaDefinition;

public enum SiparisStatus {
	SIPARIS_ALINDI,
    ODEME_ALINDI,
    ODEME_BASARISIZ,
    HAZIRLANIYOR,
    SIPARIS_KARGODA,
    SIPARIS_TESLIM_EDILDI,
    IPTAL_EDILDI,

    IADE_BEKLENIYOR,
    IADE_KABUL_EDILDI,
    IADE_REDDEDILDI,
    IADE_KARGODA,
    IADE_TESLIM_EDILDI,
    IADE_EDILDI,
}
