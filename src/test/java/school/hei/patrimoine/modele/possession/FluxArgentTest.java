package school.hei.patrimoine.modele.possession;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FluxArgentTest {
    @Test
    void train_de_vie_est_finance_par_compte_courant() {
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var compteCourant = new Argent("Compte courant", au13mai24, 600_000);

        var aLOuvertureDeITM = LocalDate.of(2021, OCTOBER, 26);
        var aLaDiplomation = LocalDate.of(2024, DECEMBER, 26);
        var vieEstudiantine =
                new FluxArgent(
                        "Ma super(?) vie d'etudiant",
                        compteCourant,
                        aLOuvertureDeITM,
                        aLaDiplomation,
                        -500_000,
                        1);
        var donsDePapaEtMamanAuDebut =
                new FluxArgent(
                        "La générosité des parents au début",
                        compteCourant,
                        aLOuvertureDeITM,
                        aLOuvertureDeITM.plusDays(100),
                        400_000,
                        30);
        var donsDePapaEtMamanALaFin =
                new FluxArgent(
                        "La générosité des parents à la fin",
                        compteCourant,
                        aLaDiplomation,
                        aLaDiplomation.minusDays(100),
                        400_000,
                        30);

        assertEquals(0, compteCourant.projectionFuture(au13mai24.minusDays(100)).valeurComptable);
        assertEquals(600_000, compteCourant.projectionFuture(au13mai24).valeurComptable);
        var au26juin24 = LocalDate.of(2024, JUNE, 26);
        assertEquals(100_000, compteCourant.projectionFuture(au26juin24).valeurComptable);
        assertEquals(-2_900_000, compteCourant.projectionFuture(aLaDiplomation).valeurComptable);
        assertEquals(
                -2_900_000, compteCourant.projectionFuture(aLaDiplomation.plusDays(100)).valeurComptable);
    }

    @Test
    void fluxArgentProjectionFutureTest() {
        LocalDate debut = LocalDate.of(2024, MAY, 1);
        LocalDate fin = LocalDate.of(2024, MAY, 31);
        LocalDate tFutur = LocalDate.of(2024, JUNE, 1);
        Argent argent = new Argent("Argent", debut, 1000);
        FluxArgent fluxArgent = new FluxArgent("Flux", argent, debut, fin, 100, debut.getDayOfMonth());

        FluxArgent fluxArgentFutur = (FluxArgent) fluxArgent.projectionFuture(tFutur);

        assertEquals(1100, fluxArgentFutur.getArgent().getValeurComptable(), "La valeur comptable future de l'argent dans le flux devrait être 1100.");
    }

    @Test
    void projectionFutureFinAvantPeriode() {
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var dateFin = au13mai24.minusDays(1);
        var argent = new Argent("Espèces", au13mai24, 600_000);
        var fluxArgent = new FluxArgent(
                "Flux Fin Avant",
                argent,
                au13mai24.minusDays(10),
                dateFin,
                -100_000,
                15
        );

        var fluxArgentFuture = fluxArgent.projectionFuture(au13mai24.plusDays(10));

        assertEquals(argent.nom, fluxArgentFuture.getArgent().getNom());
        assertEquals(600_000, fluxArgentFuture.getArgent().getValeurComptable());
    }

    @Test
    void projectionFutureDateOperationDernierJour() {
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var argent = new Argent("Espèces", au13mai24, 600_000);
        var fluxArgent = new FluxArgent(
                "Flux Fin Mois",
                argent,
                au13mai24.minusDays(30),
                au13mai24.plusDays(30),
                -50_000,
                31 // dernier jour du mois
        );

        var fluxArgentFuture = fluxArgent.projectionFuture(au13mai24.plusMonths(1));

        assertTrue(fluxArgentFuture.getArgent().getValeurComptable() < 600_000, "La valeur comptable devrait diminuer.");
    }

}
