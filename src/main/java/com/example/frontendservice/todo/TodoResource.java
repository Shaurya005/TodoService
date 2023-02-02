package com.example.frontendservice.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200/") // Without this, cross-origin communication is not allowed i.e. with frontend.
// Access to XMLHttpRequest at 'http://localhost:8080/users/in28minutes/todos' from origin 'http://localhost:4200' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
public class TodoResource
{
    @Autowired
    private TodoHardcodedService todoService;

    @GetMapping("/users/{username}/todos")
    public List<Todo> getAllTodos(@PathVariable String username) throws InterruptedException {
        // Thread.sleep(3000);
        return todoService.findAll();
    }

    /*
    Typically what should be the response to it, is that we should return from a delete method.
There are two options: either we can return success or we can return no content back.
So in this specific thing we choose the no content option i.e. ResponseEntity without any content. So ResponseEntity, enable us to return a specific status back.
In above GetMapping we returned the success status back. So we did not really need to use the response entity, because that's the default.
But now we want to return a specific status of no content back, and that's the reason why we are using ResponseEntity<void>.
     */
    @DeleteMapping("/users/{username}/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String username, @PathVariable long id) {
       Todo todo = todoService.deleteById(id);

       // ResponseEntity uses Builder Pattern
       if(todo != null) {
           return ResponseEntity.noContent().build();
       }
       /*
       We are returning a status of noContent if you are able to successfully delete it. Otherwise it's returning notFound status i.e. responseEntity notFound().build() status.
       So what we are doing in here is we are invoking the todo service delete method. If delete was successful, this would return a todo back.

       And in other situation, we are retaining no content back. Otherwise we are returning not fund status back.
       One of the important parts of building RESTful services is deciding what status to use and ResponseEntity helps us to build specific
       requests with specific statuses. And that's the reason why we are making use of response entity in this specific request.
         */

       return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{username}/todos/{id}")
    public Todo getTodo(@PathVariable String username, @PathVariable long id) throws InterruptedException {
        return todoService.findById(id);
    }

    @PutMapping("/users/{username}/todos/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable String username, @PathVariable long id, @RequestBody Todo todo) {

        Todo todoUpdated = todoService.save(todo);

        return new ResponseEntity<>(todoUpdated, HttpStatus.OK);
        /*
        Return a new Response entity containing a todo. We populate in the todo and say Http Status.OK.
        Actually the same thing can be done by just saying return todo and changing the return type to Todo as well. But this gives us more options.
        In future if you want to extend this and be able to return other status back then using the response entity gives us more options.
         */
    }

    @PostMapping("/users/{username}/todos")
    public ResponseEntity<Void> updateTodo(@PathVariable String username, @RequestBody Todo todo)
    {
        Todo todoCreated = todoService.save(todo);
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