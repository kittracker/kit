export default async () => {
    const response = await fetch("/api/users", { method: "GET" }); // Corrected fetch
    const users = await response.json(); // Parse JSON response

    console.log(users); // Debugging output

    return `
        <div class="container mt-5">
            <br />
            <h1 class="mb-4">USERS</h1>
            <br />
            ${users.map(user => `
                <div class="card mb-3 list-card" href="/users/${user.id}" data-link>
                    <div class="card-body">
                        <h5 class="card-title">@${user.username}</h5>
                    </div>
                </div>
            `).join("")}
        </div>
    `;
};
