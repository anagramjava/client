package com.socgen.test.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
public class Application implements CommandLineRunner {


    @Autowired
    private RestTemplate restTemplate;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);
        return requestFactory;
    }

    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factor) {
        RestTemplate restTemplate = new RestTemplate(factor);
        return restTemplate;
    }


    //access command line arguments
    @Override
    public void run(String... args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            boolean ended = false;
            while (!ended) {
                System.out.println("Select any of the option. Press ctrl+c to end ");
                System.out.println("[A]\tAdd a word");
                System.out.println("[D]\tDelete a word");
                System.out.println("[P]\tPrint anagrams");
                System.out.println("[E]\tEnd");

                String choice = in.readLine();

                switch (choice.toUpperCase()) {

                    case "A":
                        boolean added = addWord(getWord(in));
                        System.out.println(added ? "added" : "already present");
                        break;
                    case "D":
                        boolean removed = removeWord(getWord(in));
                        System.out.println(removed ? "removed" : "not present");
                        break;
                    case "P":
                        String[] words = getAnagrams(getWord(in));
                        Arrays.stream(words).forEach(System.out::println);
                        break;
                    case "E":
                        ended = true;
                        break;
                    default:
                        System.out.println("Invalid choice please select again.");
                        break;
                }
            }
        } catch (Exception exception) {
            System.out.println("Error while performing action");
            System.out.println("Please enter any key");
            in.readLine();
        }
    }

    private String getWord(BufferedReader in) throws IOException {
        System.out.println("Enter a word ");
        String word = in.readLine();
        boolean wordFound = false;
        do {
            if (word == null || word.isEmpty()) {
                System.out.println("Enter a word. ");
                word = in.readLine();
            } else {
                wordFound = true;
            }
        } while (!wordFound);
        return word;
    }

    private boolean removeWord(String word) {

        String url
                = "http://localhost:8080/words";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("word", word);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE,
                request, Boolean.class);

        return responseEntity.getBody();
    }

    private boolean addWord(String word) {
        String url
                = "http://localhost:8080/words";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("word", word);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
        return response.getBody();

    }


    private String[] getAnagrams(String word) {
        String url
                = "http://localhost:8080/anagdddrams/" + word;
        ResponseEntity<String[]> response
                = restTemplate.getForEntity(url, String[].class);
        return response.getBody();
    }
}
