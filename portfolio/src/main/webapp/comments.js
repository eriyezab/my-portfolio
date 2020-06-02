function getComments() {
  fetch("/data")
  .then(res => res.json())
  .then((comments) => {
    console.log("Retrieved comments from server.")
    console.log(comments);
    for(i = 0; i < comments.length; ++i) {
      const commentsList = document.getElementById('comments-list');
      let listNode = document.createElement("LI");
      let textNode = document.createTextNode(comments[i]);
      listNode.appendChild(textNode);
      commentsList.appendChild(listNode);
    }
  });
}

getComments();


