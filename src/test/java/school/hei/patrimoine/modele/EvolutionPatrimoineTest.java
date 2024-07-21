package school.hei.patrimoine.modele;

import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.possession.Argent;
import school.hei.patrimoine.modele.possession.FluxArgent;
import school.hei.patrimoine.modele.possession.Materiel;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.time.Month.MAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvolutionPatrimoineTest {

    @Test
    void patrimoine_evolue() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var trainDeVie =
                new FluxArgent(
                        "Vie courante",
                        financeur,
                        au13mai24.minusDays(100),
                        au13mai24.plusDays(100),
                        -100_000,
                        15);
        var patrimoineIloAu13mai24 =
                new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur, trainDeVie));

        var evolutionPatrimoine =
                new EvolutionPatrimoine(
                        "Nom",
                        patrimoineIloAu13mai24,
                        LocalDate.of(2024, MAY, 12),
                        LocalDate.of(2024, MAY, 17));

        var evolutionJournaliere = evolutionPatrimoine.getEvolutionJournaliere();
        assertEquals(0, evolutionJournaliere.get(LocalDate.of(2024, MAY, 12)).getValeurComptable());
        assertEquals(
                600_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 13)).getValeurComptable());
        assertEquals(
                600_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 14)).getValeurComptable());
        assertEquals(
                500_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 15)).getValeurComptable());
        assertEquals(
                500_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 16)).getValeurComptable());
        assertEquals(
                500_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 17)).getValeurComptable());
    }

    @Test
    void serieValeursComptablesPatrimoine() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var trainDeVie =
                new FluxArgent(
                        "Vie courante",
                        financeur,
                        au13mai24.minusDays(100),
                        au13mai24.plusDays(100),
                        -100_000,
                        15);
        var patrimoineIloAu13mai24 =
                new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur, trainDeVie));

        var evolutionPatrimoine =
                new EvolutionPatrimoine(
                        "Nom",
                        patrimoineIloAu13mai24,
                        LocalDate.of(2024, MAY, 12),
                        LocalDate.of(2024, MAY, 17));

        List<Integer> expectedSerie = List.of(0, 600_000, 600_000, 500_000, 500_000, 500_000);
        assertEquals(expectedSerie, evolutionPatrimoine.serieValeursComptablesPatrimoine());
    }

    @Test
    void fluxImpossibles_vide() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var patrimoineIloAu13mai24 =
                new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur));

        var evolutionPatrimoine =
                new EvolutionPatrimoine(
                        "Nom",
                        patrimoineIloAu13mai24,
                        LocalDate.of(2024, MAY, 12),
                        LocalDate.of(2024, MAY, 17));

        var fluxImpossibles = evolutionPatrimoine.getFluxImpossibles();
        assertTrue(fluxImpossibles.isEmpty(), "Il ne devrait y avoir aucun flux impossible.");
    }

    @Test
    void serieValeursComptablesParPossessionPatrimoineVideTest() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var patrimoineVide = new Patrimoine("patrimoineVide", ilo, au13mai24, Set.of());

        var evolutionPatrimoine =
                new EvolutionPatrimoine(
                        "Nom",
                        patrimoineVide,
                        LocalDate.of(2024, MAY, 1),
                        LocalDate.of(2024, MAY, 20));

        var serieValeurs = evolutionPatrimoine.serieValeursComptablesParPossession();
        assertTrue(serieValeurs.isEmpty(), "La série des valeurs comptables doit être vide pour un patrimoine vide.");
    }

    @Test
    void serieValeursComptablesParPossessionSansEvolutionTest() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var patrimoineIloAu13mai24 =
                new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur));

        var evolutionPatrimoine =
                new EvolutionPatrimoine(
                        "Nom",
                        patrimoineIloAu13mai24,
                        LocalDate.of(2024, MAY, 13),
                        LocalDate.of(2024, MAY, 13));

        var serieValeurs = evolutionPatrimoine.serieValeursComptablesParPossession();
        assertTrue(serieValeurs.containsKey(financeur), "La série des valeurs comptables doit contenir l'objet 'financeur'.");
        assertEquals(
                List.of(600_000),
                serieValeurs.get(financeur),
                "Les valeurs comptables du 'financeur' ne correspondent pas pour une période sans évolution.");
    }

    @Test
    void fluxArgentDepassePeriodTest() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var fluxArgent = new FluxArgent(
                "Flux Dépassé",
                financeur,
                au13mai24.minusDays(200),
                au13mai24.plusDays(200),
                -100_000,
                15
        );
        var patrimoineIloAu13mai24 = new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur, fluxArgent));

        var evolutionPatrimoine = new EvolutionPatrimoine(
                "Nom",
                patrimoineIloAu13mai24,
                LocalDate.of(2024, MAY, 12),
                LocalDate.of(2024, MAY, 17)
        );

        var evolutionJournaliere = evolutionPatrimoine.getEvolutionJournaliere();
        assertEquals(0, evolutionJournaliere.get(LocalDate.of(2024, MAY, 12)).getValeurComptable());
        assertEquals(600_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 13)).getValeurComptable());
        assertEquals(600_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 14)).getValeurComptable());
        assertEquals(500_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 15)).getValeurComptable());
        assertEquals(500_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 16)).getValeurComptable());
        assertEquals(500_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 17)).getValeurComptable());
    }

    @Test
    void possessionsInchangeantesTest() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var patrimoineIloAu13mai24 = new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur));

        var evolutionPatrimoine = new EvolutionPatrimoine(
                "Nom",
                patrimoineIloAu13mai24,
                LocalDate.of(2024, MAY, 13),
                LocalDate.of(2024, MAY, 13)
        );

        var evolutionJournaliere = evolutionPatrimoine.getEvolutionJournaliere();
        assertEquals(600_000, evolutionJournaliere.get(LocalDate.of(2024, MAY, 13)).getValeurComptable());
    }

    @Test
    void possessionsEvolutivesTest() {
        var ilo = new Personne("Ilo");
        var au13mai24 = LocalDate.of(2024, MAY, 13);
        var financeur = new Argent("Espèces", au13mai24, 600_000);
        var materiel = new Materiel("Matériel", au13mai24, 1_000, au13mai24.minusMonths(1), 0.05);
        var patrimoineIloAu13mai24 = new Patrimoine("patrimoineIloAu13mai24", ilo, au13mai24, Set.of(financeur, materiel));

        var evolutionPatrimoine = new EvolutionPatrimoine(
                "Nom",
                patrimoineIloAu13mai24,
                LocalDate.of(2024, MAY, 13),
                LocalDate.of(2024, MAY, 17)
        );

        var evolutionJournaliere = evolutionPatrimoine.getEvolutionJournaliere();
        assertTrue(evolutionJournaliere.get(LocalDate.of(2024, MAY, 13)).getValeurComptable() > 600_000);
        assertTrue(evolutionJournaliere.get(LocalDate.of(2024, MAY, 17)).getValeurComptable() > 600_000);
    }

    @Test
    void possessionDateAcquisitionFutureTest() {
        var ilo = new Personne("Ilo");
        var futureDate = LocalDate.of(2024, MAY, 20);
        var acquisitionFuture = LocalDate.of(2024, MAY, 30);
        var financeur = new Argent("Espèces", futureDate, 600_000);
        var materiel = new Materiel("Matériel", futureDate, 1_000, acquisitionFuture, 0.05);
        var patrimoineIloAu13mai24 = new Patrimoine("patrimoineIloAu13mai24", ilo, futureDate, Set.of(financeur, materiel));

        var evolutionPatrimoine = new EvolutionPatrimoine(
                "Nom",
                patrimoineIloAu13mai24,
                LocalDate.of(2024, MAY, 13),
                LocalDate.of(2024, MAY, 17)
        );

        var evolutionJournaliere = evolutionPatrimoine.getEvolutionJournaliere();
        assertEquals(0, evolutionJournaliere.get(LocalDate.of(2024, MAY, 13)).getValeurComptable());
        assertEquals(0, evolutionJournaliere.get(LocalDate.of(2024, MAY, 17)).getValeurComptable());
    }

}
