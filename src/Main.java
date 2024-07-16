import javax.sound.midi.*;
import java.io.IOException;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main{

    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException, IOException {
        // Воспроизведение мелодии из файла MIDI
        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        Sequence sequence = MidiSystem.getSequence(Main.class.getResourceAsStream("Polka2.midi"));
        sequencer.setSequence(sequence);
        sequencer.start();
        var quit = true;
        while (quit) {
            if (!sequencer.isRunning()) {
                sequencer.close();
                quit = false;
            }
        }
        Thread.sleep(1000); // Ждать указанное время

        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        Receiver receiver = synth.getReceiver();

        ShortMessage programChange = new ShortMessage();
        programChange.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
        receiver.send(programChange, -1);

        var content = new String(Files.readAllBytes(Paths.get("D:\\IdeaProjects\\New-hexlet-java-midi\\src\\Melody.txt")));
        readString(receiver, content); // Записываем в мелодию

        System.out.println("Please, enter notes:");
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        while (!input.equals("q")) {
            readString(receiver, input); // Записываем в мелодию
            System.out.println("Please, enter notes:");
            input = scanner.nextLine();
        }

        scanner.close();
        synth.close();
        System.out.println("See you!");
    }

    private static void readString(Receiver receiver, String content) throws InvalidMidiDataException, InterruptedException {
        var notes = content.split(",");
        var i = 0;
        while (i < notes.length) {
            int noteCode = convertNote(notes[i].charAt(0), 12 * ( Integer.parseInt(String.valueOf(notes[i].charAt(1))))); // Основание ноты
            int duration = Integer.parseInt(notes[i].substring(2));
            playNote(receiver, noteCode, duration);
            i++;
        }

    }

    // Метод для преобразования символа ноты в код ноты
    private static int convertNote(char note, int octava) {
        if (octava < 105) {
            switch (note) {
                case 'C', 'c':
                    return 0 + octava;
                case 'D', 'd':
                    return 2 + octava;
                case 'E', 'e':
                    return 4 + octava;
                case 'F', 'f':
                    return 5 + octava;
                case 'G', 'g':
                    return 7 + octava;
                case 'A', 'a':
                    return 9 + octava;
                case 'B', 'b':
                    return 11 + octava;
                default:
                    return 127; // Если нота не распознана, возвращается код 127
            }
        }
        return 127; // Если нота не распознана, возвращается код 127
    }

    // Метод для воспроизведения ноты
    private static void playNote(Receiver receiver, int note, int duration)
            throws InterruptedException, InvalidMidiDataException {
        receiver.send(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 100),
                -1); // Начать звучание ноты
        Thread.sleep(duration); // Ждать указанное время

        receiver.send(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 100),
                -1); // Остановить звучание ноты
    }
}