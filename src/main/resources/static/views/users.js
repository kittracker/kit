export default async () => {
    const response = await fetch("/users", { method: "GET" }); // Corrected fetch
    const users = await response.json(); // Parse JSON response

    console.log(users); // Debugging output

    return `
        <div class="container mt-5">
            <h1 class="mb-4">Users List</h1>
            <ul class="list-group">
                ${users.map(user => `
                    <li class="list-group-item">
                        <a href="/users/${user.id}" data-link>@${user.username}</a>
                    </li>
                `).join("")}
            </ul>
        </div>
    `;
};
