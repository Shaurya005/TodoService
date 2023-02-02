package com.example.frontendservice.jpa;

import com.example.frontendservice.todo.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200/") // Without this, cross-origin communication is not allowed i.e. with frontend.
// Access to XMLHttpRequest at 'http://localhost:8080/jpa/users/in28minutes/todos' from origin 'http://localhost:4200' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
public class TodoJpaResource
{
    @Autowired
    private TodoJpaRepository todoJpaRepository;

    @GetMapping("/jpa/users/{username}/todos")
    public List<Todo> getAllTodos(@PathVariable String username) throws InterruptedException {
        return todoJpaRepository.findByUsername(username);
    }

    /*
    Typically what should be the response to it, is that we should return from a delete method.
    There are two options: either we can return success or we can return no content back.
    So in this specific thing we choose the no content option i.e. ResponseEntity without any content. So ResponseEntity, enable us to return a specific status back.
    In above GetMapping we returned the success status back. So we did not really need to use the response entity, because that's the default.
    But now we want to return a specific status of no content back, and that's the reason why we are using ResponseEntity<void>.
     */
    @DeleteMapping("/jpa/users/{username}/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String username, @PathVariable long id) {

        todoJpaRepository.deleteById(id);

       return ResponseEntity.noContent().build();
    }

    @GetMapping("/jpa/users/{username}/todos/{id}")
    public Todo getTodo(@PathVariable String username, @PathVariable long id) throws InterruptedException {
        return todoJpaRepository.findById(id).get();
    }

    @PutMapping("/jpa/users/{username}/todos/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable String username, @PathVariable long id, @RequestBody Todo todo)
    {
        todo.setUsername(username);
        Todo todoUpdated = todoJpaRepository.save(todo);

        return new ResponseEntity<>(todoUpdated, HttpStatus.OK);
        /*
        Return a new Response entity containing a todo. We populate in the todo and say Http Status.OK.
        Actually the same thing can be done by just saying return todo and changing the return type to Todo as well. But this gives us more options.
        In future if you want to extend this and be able to return other status back then using the response entity gives us more options.
         */
    }

    @PostMapping("/jpa/users/{username}/todos")
    public ResponseEntity<Void> createTodo(@PathVariable String username, @RequestBody Todo todo)
    {
        todo.setUsername(username);

        Todo todoCreated = todoJpaRepository.save(todo);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(todoCreated.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
    /*
    we are trying to follow the REST Api standards. The delete method typically returns no content when it's successful.
    And if it's failing, then it would return not found. The PUT request would return a status of OK with the content of the updated resource.
    When you tried to a POST, it returns the status as created, and returns the URL of the created resource.
    These are basically the standards which I used when we create resources. We want to update the todo, and create todo,
    so we quickly created these services so that we can focus on the front end in the next few steps.
     */
}